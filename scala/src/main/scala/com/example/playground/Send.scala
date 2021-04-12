package com.example.playground


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
  ): retrieveFunctions.Module = {
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
