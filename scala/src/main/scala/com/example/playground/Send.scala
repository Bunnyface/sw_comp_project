package com.example.playground


object sendFunctions {
  def queryInsert(table: String, data: Array[String]): Array[String] = {
    val row = "'" + data.mkString("', '") + "'";
    val query = f"INSERT INTO $table%s VALUES ($row%s);";

    val sqlClient = new Client();
    sqlClient.connect("defaultdb");
 
    try {
        sqlClient.execute(query);
        sqlClient.close();
        return data;
    } catch {
        case _: Throwable => sqlClient.close();
    }
    return null;
  }

  def queryUpdate(
    table: String, 
    newValCol: String, 
    newVal: String,
    condCol: String,
    condVal: String
  ): Map[String, Array[String]] = {
    val sqlClient = new Client();
    sqlClient.connect("defaultdb");

    val change = f"${newValCol}='${newVal}'";
    val condition = f"${condCol}='${condVal}'";
    val query = f"UPDATE $table%s SET $change%s WHERE $condition%s;";

    try {
        sqlClient.execute(query);
        val created = retrieveFunctions.queryRelease(condVal);
        sqlClient.close();
        return created;
    } catch {
        case _: Throwable => sqlClient.close();
    }
    return null;
  }
}
