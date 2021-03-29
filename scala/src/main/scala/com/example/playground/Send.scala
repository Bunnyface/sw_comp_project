package com.example.playground

import client.Client


object sendFunctions {
  def queryInsert(table: String, data: Array[String]): Int = {
    val row = "'" + data.mkString("', '") + "'";
    val query = f"INSERT INTO $table%s VALUES ($row%s);";

    var sqlClient = new Client();
    sqlClient.connect("postgres");

    try {
        sqlClient.execute(query);
        sqlClient.close();
        return 1;
    } catch {
        case _: Throwable => sqlClient.close();
    }
    return 0;
  }

  def queryUpdate(table: String, newVal: String, condition: String): Int = {
    val sqlClient = new Client();
    sqlClient.connect("postgres");
    
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
