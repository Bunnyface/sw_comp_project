package com.example.playground

import client.Client


object sendFunctions {
  def queryExecute(queryData: String): Int = {
    var sqlClient = new Client();
    sqlClient.connect("defaultdb", "scalauser", "example");
    
    val data = "'" + queryData.replace(":", "', '") + "'";
    val query = f"INSERT INTO releases VALUES ($data%s);";

    try {
        sqlClient.execute(query);
        sqlClient.close();
        return 1;

    } catch {
        case _: Throwable => {
            sqlClient.close();
            return 0;
        }
    }
  }

  def queryUpdate(queryData: String, newValType: String): Int = {
    var sqlClient = new Client();
    sqlClient.connect("defaultdb", "scalauser", "example");

    val params = queryData.split(":");
    val newVal = params(0);
    val condition = params(1);

    val query = f"UPDATE releases SET $newVal%s WHERE $condition%s;";

    try {
        sqlClient.execute(query);
        sqlClient.close();
        return 1;

    } catch {
        case _: Throwable => {
            sqlClient.close();
            return 0;
        }
    }
  }
}
