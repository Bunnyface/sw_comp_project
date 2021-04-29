package com.example.playground

import io.circe._
import io.circe.syntax._
import retrieveFunctions._

object sendFunctions {
  def queryInsert(
    table: String,
    columns: Array[String],
    data: Array[Array[String]]
  ): Array[Array[String]] = {
    if (data == null || data.length == 0)
      return Array();

    val sqlClient = new Client();
    sqlClient.connect("defaultdb");

    val columnString = columns.mkString(", ");

    val result: Array[Array[String]] = data.map(array => {
      val row = "'" + array.mkString("', '") + "'";
      val query = f"INSERT INTO $table%s ($columnString%s) VALUES ($row%s);"; 

      try {
        val res = sqlClient.execute(query);
        array;
      } catch {
        case _: Throwable => null;
      }
    }).filter(row => row != null);

    sqlClient.close();
    return result;
  }

  def queryUpdate(
    table: String, 
    newValCol: String, 
    newVal: String,
    condCol: String,
    condVal: String
  ): Json = {
    val sqlClient = new Client();
    sqlClient.connect("defaultdb");


    val change = f"${newValCol}='${newVal}'";
    val condition = f"${condCol}='${condVal}'";
    val query = f"UPDATE $table%s SET $change%s, row_version=row_version+1 WHERE $condition%s RETURNING row_version;";

    val version = getArray(table, "row_version", condition)(0)(0).asInstanceOf[Int];

    try {
      val returnedRow = sqlClient.execute(query);
      returnedRow.next();
      val updatedVersion = returnedRow.getInt(1);
      if (updatedVersion - version != 1) {
        sqlClient.rollback();
        throw new Exception("Update wasn't successful, performing the rollback..");
      }

      val created = getMapArray(table, "*", condition)
        .map(row => mapToJson(row))
        .asJson;
      sqlClient.close();
      return created;
    } catch {
      case _: Throwable => 
        println("Update wasn't successful");
        sqlClient.close();
    }
    return null;
  }
}
