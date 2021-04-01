import os
import json
import requests
import pytest
import logging
import asyncio
import asyncpg

SCALAURL = "http://scala:8081" # TODO ADD SOME EASY WAYS TO CUSTOMIZE

DB_NAME = "defaultdb"
DB_USER = os.getenv('PSQLUSER')
DB_HOST = os.getenv('PSQLHOST')
DB_PASSWD = os.getenv('PSQLPASSWD')


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
                return list(await conn.fetchrow(query))
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
    assert(expected.code == received.status_code)
    assert(expected.body == body)

# --------------- Tests ---------------

def test_releases():
    conn = get_db_connection()

    # Test 1: Fetch all the release names.
    exp = [x[0] for x in fetch(conn, "SELECT name FROM releases;", "all")]
    perform_test(
        "/releases", 
        "GET", 
        Expected(200, exp),
        "Sending request for releases"
    )

    # Test 2: Fetch all the components individually.
    names = list(exp)
    for name in names:
        name_version = fetch(
            conn, 
            f"SELECT name, version FROM releases WHERE name='{name}';", 
            "row"
        )
        components = fetch(
            conn,
            f"SELECT componentName FROM junctionTable WHERE releasename='{name}';",
            "all"
        )
        exp = {
            "info": name_version, 
            "components": [x[0] for x in components]
        }
        perform_test(
            f"/releases/{name}", 
            "GET",
            Expected(200, exp),
            f"Sending request for release info on {name}"
        )

    close_db_connection(conn)

def test_insert():
    conn = get_db_connection()

    # Test 1: Insert a new component
    payload = {"table": "releases", "data": ["newName", "3.2.1"]}
    perform_test(
        "/insert",
        "POST",
        Expected(201, payload["data"]),
        "Inserting a new release",
        payload
    )

    exp = {
        "components": [], 
        "info": payload["data"]
    }
    perform_test(
        "/releases/newName",
        "GET",
        Expected(200, exp),
        "Fetching a newly created release"
    )

    # Test 2: Insert a new component to a nonexistent table
    payload["table"] = "rel_eases"
    payload["data"] = ["new", "1.2"]
    perform_test(
        "/insert",
        "POST",
        Expected(400, ""),
        "Inserting an incorrectly formatted new release",
        payload
    )

    perform_test(
        "/releases/new",
        "GET",
        Expected(404, ""),
        "Trying to get a nonexistent component",
    )

    close_db_connection(conn)

def test_update():
    conn = get_db_connection()

    # Test 1: Updating component's version.
    payload = {
        "table": "releases",
        "newValCol": "version",
        "newVal": "2.1",
        "condCol": "name",
        "condVal": "newName"
    }
    exp = {
        "components": [], 
        "info": ["newName", "2.1"]
    }
    perform_test(
        "/update",
        "POST",
        Expected(201, exp),
        "Updating components' version",
        payload
    )
    perform_test(
        "/releases/newName",
        "GET",
        Expected(200, exp),
        "Getting updated components' version",
    )

    # Test 2: Trying to update a component's version incorrectly.
    payload = {
        "table": "releases",
        "newValCol": "ver_sion",
        "newVal": "123.123.123",
        "condCol": "na_me",
        "condVal": "newName"
    }
    perform_test(
        "/update",
        "POST",
        Expected(400, ''),
        "Updating components' version using incorrect data",
        payload
    )
    perform_test(
        "/releases/newName",
        "GET",
        Expected(200, exp),
        "Trying to get incorrectly updated components' version",
    )

    close_db_connection(conn)
