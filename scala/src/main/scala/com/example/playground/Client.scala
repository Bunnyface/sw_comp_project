package com.example.playground

import java.sql.{Connection, DriverManager, ResultSet}


class Client {
  var connection: Connection = null;

  def connect(dbname: String, dbuser: String = null, passwd: String = null) {
    classOf[org.postgresql.Driver];
    val (host, user, password) = getConnectionData();
    if (dbuser != null && passwd != null){
      val connString = f"jdbc:postgresql://$host%s/$dbname%s";
      connection = DriverManager.getConnection(connString, dbuser, passwd);
    }
    else {
      val connString = f"jdbc:postgresql://$host%s/$dbname%s";
      connection = DriverManager.getConnection(connString, user, password);
    }
  }

  def execute(query: String) {
    if (connection != null) {
      val stm = connection.createStatement(
        ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_UPDATABLE);
      stm.executeUpdate(query);
    }
    else {
      println("Connection was not established.");
    }
  }

  def fetch(query: String): ResultSet = {
    if (connection != null) {
      val stm = connection.createStatement(
        ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
      val result = stm.executeQuery(query);
      return result;
    }
    else {
      println("Connection was not established.");
      return null;
    }
  }

  def close() {
    if (connection != null) 
      connection.close();
    else 
      println("Connection was not established.");
  }

  def getConnectionData(): (String, String, String) = {
    val host = sys.env("PSQLHOST");
    val user = sys.env("PSQLUSER");
    val passwd = sys.env("PSQLPASSWD");
    return (host, user, passwd);
  }
}
