import os
import json
import requests
import pytest
import logging
import asyncio
import asyncpg
import threading
import time

SCALAURL = "http://scala:8081" # TODO ADD SOME EASY WAYS TO CUSTOMIZE

DB_NAME = "defaultdb"
DB_USER = os.getenv('PSQLUSER')
DB_HOST = os.getenv('PSQLHOST')
DB_PASSWD = os.getenv('PSQLPASSWD')

MODULES_TABLE_NAME = os.getenv('MODULES_TABLE')
MODULETOCOMP_TABLE_NAME = os.getenv('MODULETOCOMP_TABLE')
COMPONENT_TABLE_NAME = os.getenv('COMP_TABLE')

class Expected:
    def __init__(self, code, body):
        self.code = code
        self.body = body

# --------------- Utils ---------------

def get_db_connection():
    async def run():
        return await asyncpg.connect(user=DB_USER, password=DB_PASSWD,
                                        database=DB_NAME, host=DB_HOST)
    return asyncio.get_event_loop().run_until_complete(run())

def close_db_connection(conn):
    async def run():
        await conn.close()
    asyncio.get_event_loop().run_until_complete(run())

def fetch(conn, query, mode="row"):
    async def run():
        async with conn.transaction():
            if mode == "row":
                t = await conn.fetchrow(query)
                if t != None:
                    return list(t)
                else:
                    return t
            else:
                return [list(x) for x in await conn.fetch(query)]
    
    return asyncio.get_event_loop().run_until_complete(run())


def perform_request(url, method, payload=None):
    if method == "GET":
        return requests.get(SCALAURL + url)
    elif method == "POST":
        return requests.post(SCALAURL + url, json=payload)
    elif method == "PUT":
        return requests.put(SCALAURL + url, json=payload)    

def parse_to_json(data):
    try:
        return json.loads(data)
    except json.decoder.JSONDecodeError:
        return data

def perform_test(url, method, expected, message=None, payload=None):
    if message:
        logging.info(message)
    else:
        logging.info(f"Performing tests on {url}...")
    
    received = perform_request(url, method, payload)
    body = parse_to_json(received.text)
    logging.warning(f"received body: {body}")
    assert(expected.code == received.status_code)
    assert(expected.body == body)

def perform_parallel_test(module_id, thread_num=4):
    responses = []

    def task(cond, name):
        with cond:
            cond.wait()
        responses.append(
            perform_request(
                "/update",
                "POST",
                {
                    "table": MODULES_TABLE_NAME,
                    "newValCol": "name",
                    "newVal": name,
                    "condCol": "id",
                    "condVal": str(module_id)
                }
            )
        )

    def signal(cond):
        cond.acquire()
        cond.notify_all()
        cond.release()    

    threads = []
    condition = threading.Condition()

    for i in range(thread_num):
        threads.append(
            threading.Thread(name=f't{i}', target=task, args=(condition, f'n{i}',))
        )

    signal = threading.Thread(name='p', target=signal, args=(condition,))

    for th in threads:
        th.start()

    time.sleep(2)
    signal.start()

    for th in threads:
        th.join()
    
    signal.join()

    for r in responses[1:]:
        if responses[0].status_code != r.status_code:
            return False
    return True


# --------------- Tests ---------------

def test_releases():
    conn = get_db_connection()

    # Test 1: Fetch all the release names.
    test_string = "SELECT name FROM " + MODULES_TABLE_NAME + ";"
    exp = [x[0] for x in fetch(conn, test_string , "all")]
    perform_test(
        "/releases", 
        "POST", 
        Expected(200, exp),
        "Sending request for releases"
    )

    # Test 2: Fetch all the components individually.
    names = list(exp)
    for name in names:
        id_string = f"SELECT id FROM {MODULES_TABLE_NAME} releases WHERE name='{name}';"
        id_module = fetch(
            conn,
            id_string,
            "row"
        )

        junction_string = f"SELECT comp_id FROM {MODULETOCOMP_TABLE_NAME} WHERE module_id='{id_module[0]}';"
        comp_ids = fetch(
            conn,
            junction_string,
            "all"
        )
        flat_comps = []
        for sublist in comp_ids:
            for item in sublist:
                flat_comps.append(item)
        exp_comp = []
        if flat_comps != None:
            for c_id in flat_comps:
                comp_string = f"SELECT name, version FROM {COMPONENT_TABLE_NAME} WHERE id='{c_id}';"
                components = fetch(
                   conn,
                   comp_string,
                   "row"
                )
                exp_comp.append(components)
        exp = {
            "info": [name],
            "components": exp_comp
        }
        perform_test(
            f"/releases/{name}",
            "POST",
            Expected(200, exp),
            f"Sending request for release info on {name}"
        )

    close_db_connection(conn)

#def test_load():
#    for x in range(20):
#        test_releases()

def test_insert():
    conn = get_db_connection()

    # Test 1: Insert a new component
    payload = {"columns":["name"], "data":[["newName"]]}
    perform_test(
        "/insert/module",
        "PUT",
        Expected(201, payload["data"]),
        "Inserting a new release",
        payload
    )

    exp = {
        "components": [],
        "info": payload["data"][0]
    }

    perform_test(
        "/releases/newName",
        "POST",
        Expected(200, exp),
        "Fetching a newly created release"
    )

    # Test 2: Insert multiple new elements
    payload = {"columns":["name"] ,"data": [
        ["name1"],
        ["name2"],
        ["name3"]
    ]}
    perform_test(
        "/insert/module",
        "PUT",
        Expected(201, payload["data"]),
        "Inserting a new release",
        payload
    )

    for i in range(len(payload["data"])):
        exp = {
            "components": [],
            "info": payload["data"][i]
        }
        perform_test(
            "/releases/{}".format(payload["data"][i][0]),
            "POST",
            Expected(200, exp),
            "Fetching a newly created release"
        )

    # Test 3: Insert a new component to a nonexistent table
    payload["table"] = "rel_eases"
    payload["data"] = ["new", "1.2"]
    perform_test(
        "/insert/releases",
        "PUT",
        Expected(400, ""),
        "Inserting an incorrectly formatted new release",
        payload
    )

    perform_test(
        "/releases/new",
        "POST",
        Expected(404, ""),
        "Trying to get a nonexistent component",
    )

    close_db_connection(conn)


def test_update():
    conn = get_db_connection()

    # Test 1: Updating component's version.
    payload = {
        "table": "module",
        "newValCol": "name",
        "newVal": "newName2",
        "condCol": "name",
        "condVal": "newName"
    }
    exp = {
        "components": [],
        "info": ["newName2"]
    }
    perform_test(
        "/update",
        "POST",
        Expected(201, exp),
        "Updating components' version",
        payload
    )

    perform_test(
        "/releases/newName2",
        "POST",
        Expected(200, exp),
        "Getting updated components' version",
    )

    ## Test 2: Trying to update a component's version incorrectly.
    #payload = {
    #    "table": "releases",
    #    "newValCol": "ver_sion",
    #    "newVal": "123.123.123",
    #    "condCol": "na_me",
    #    "condVal": "newName"
    #}
    #perform_test(
    #    "/update",
    #    "POST",
    #    Expected(400, ''),
    #    "Updating components' version using incorrect data",
    #    payload
    #)
    #perform_test(
    #    "/releases/newName",
    #    "GET",
    #    Expected(200, exp),
    #    "Trying to get incorrectly updated components' version",
    #)

    close_db_connection(conn)

def test_compare():
    conn = get_db_connection()

    payload = {"first": "TestModule", "second": "Demomodule"}
    exp = {'same': [], 'ex_first': [['TestComponent', 'Version 1'], ['DemoComponent', 'Version 1']], 'ex_second': []}
    perform_test(
        "/compare",
        "POST",
        Expected(200, exp),
        "Compare two releases",
        payload
    )

    payload = {"first": "First"}
    perform_test(
        "/compare",
        "POST",
        Expected(400, ''),
        "Send only one release to Compare endpoint",
        payload
    )

    payload = {"first": 123, "second": "Second"}
    perform_test(
        "/compare",
        "POST",
        Expected(400, ''),
        "Send incorrect component name to Compare endpoint",
        payload
    )

    payload = {}
    perform_test(
        "/compare",
        "POST",
        Expected(400, ''),
        "Send empty payload to Compare endpoint",
        payload
    )

    close_db_connection(conn)