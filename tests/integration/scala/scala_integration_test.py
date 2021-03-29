import requests
import pytest
import logging
SCALAURL = "http://scala:8081" # TODO ADD SOME EASY WAYS TO CUSTOMIZE
EXPECTEDRELEASES = '["First","Second","Third"]'
EXPECTEDFIRSTINFO = '{"info":["First","0.1"],"components":["Java","Scala"]}'

def test_releases_all_ok():
    logging.info("Sending request for releases")
    test_request = requests.get(SCALAURL + "/releases")
    assert(test_request.status_code == 200)
    assert(test_request.text == EXPECTEDRELEASES)

def test_releaseinfo_all_ok():
    item = "First"
    logging.info("Sending request for releaseinfo on " + item)
    test_request = requests.get(SCALAURL + "/releases/" + item)
    assert(test_request.status_code == 200)
    assert(test_request.text == EXPECTEDFIRSTINFO)