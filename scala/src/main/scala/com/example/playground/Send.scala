package com.example.playground

import client.Client


object sendFunctions {
  def queryInsert(queryData: String): Int = {
    val data = queryData.split("_");
    if (data.length != 2)
      return 0;

    val table = data(0);
    val row = "'" + data(1).replace(":", "', '") + "'";
    val query = f"INSERT INTO $table%s VALUES ($row%s);";

    var sqlClient = new Client();
    sqlClient.connect("defaultdb", "scalauser", "example");

    try {
        sqlClient.execute(query);
        sqlClient.close();
        return 1;
    } catch {
        case _: Throwable => sqlClient.close();
    }
    return 0;
  }

  def queryUpdate(queryData: String): Int = {
    val params = queryData.split(":");
    if (params.length != 3) 
      return 0;

    val sqlClient = new Client();
    sqlClient.connect("defaultdb", "scalauser", "example");
    
    val table = params(0);
    val newVal = params(1);
    val condition = params(2);

    val query = f"UPDATE $table%s SET $newVal%s WHERE $condition%s;";

    try {
        sqlClient.execute(query);
        sqlClient.close();
        return 1;
    } catch {
        case _: Throwable => sqlClient.close();
    }
    return 0;
  }
}
