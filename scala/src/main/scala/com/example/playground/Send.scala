package com.example.playground

import io.circe._
import io.circe.syntax._
import retrieveFunctions._

object sendFunctions {
  def queryInsert(
    table: String,
    columns: Array[String],
    data: Array[Array[String]]
  ): Array[Json] = {
    if (data == null || data.length == 0)
      return Array();

    val sqlClient = new Client();
    sqlClient.connect("defaultdb");

    val columnString = columns.mkString(", ");

    val result: Array[Json] = data.map(array => {
      val row = "'" + array.mkString("', '") + "'";
      val query = f"INSERT INTO $table%s ($columnString%s) VALUES ($row%s) RETURNING *;"; 

      try {
        mapToJson(
          resultSetToMapArray(
            sqlClient.execute(query)
          )(0)
        );
      } catch {
        case _: Throwable => 
        println("Insert wasn't successful");
        null;
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
    println("Sending update query");

    val change = f"${newValCol}='${newVal}'";
    val condition = f"${condCol}='${condVal}'";
    val query = f"UPDATE $table%s SET $change%s, row_version=row_version+1 WHERE $condition%s RETURNING *;";

    val version = getArray(table, "row_version", condition)(0)(0).asInstanceOf[Int];

    try {
      val returnedRow = resultSetToMapArray(
        sqlClient.execute(query)
      )(0);
      val updatedVersion = returnedRow.apply("row_version").asInstanceOf[Int];
      if (updatedVersion - version != 1) {
        sqlClient.rollback();
        throw new Exception("Parallel update occurred, performing the rollback..");
      }
      sqlClient.close();
      return valueToJson(returnedRow);
    } catch {
      case e: Exception =>
        println(e)
        println("Update wasn't successful")
    }
    sqlClient.close();
    println("RETURNING NULL");
    return null;
  }
}
