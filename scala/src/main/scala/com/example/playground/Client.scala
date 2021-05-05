package com.example.playground

import java.sql.{Connection, DriverManager, ResultSet}


class Client {
  var connection: Connection = null;

  private[this] def defaultConnecting(connString: String, user: String, passwd: String ) : Connection = {
    print(connString, user, passwd)
    val result = DriverManager.getConnection(connString, user, passwd);
    return result;
  }

  //connectFunction can be changed for tests
  def connect(dbname: String, dbuser: String = null, passwd: String = null, connectFunction: (String, String, String) => Connection = defaultConnecting) {
    //classOf[org.postgresql.Driver];
    print(classOf[DriverManager])
    val (host, user, password) = getConnectionData();
    if (dbuser != null && passwd != null){
      val connString = f"jdbc:postgresql://$host%s/$dbname%s";
      connection = connectFunction(connString, dbuser, passwd);
    }
    else {
      val connString = f"jdbc:postgresql://$host%s/$dbname%s";
      connection = connectFunction(connString, user, password);
    }
    connection.setAutoCommit(false);
  }

  def execute(query: String): ResultSet = {
    if (connection != null) {
      val result = connection.createStatement(
        ResultSet.TYPE_FORWARD_ONLY,
        ResultSet.CONCUR_UPDATABLE
      ).executeQuery(query);
      connection.commit();
      return result;
    }
    else {
      println("Connection was not established.");
      return null;
    }
  }

  def fetch(query: String): ResultSet = {
    if (connection != null) {
      val result = connection.createStatement(
        ResultSet.TYPE_SCROLL_INSENSITIVE,
        ResultSet.CONCUR_READ_ONLY
      ).executeQuery(query);
      connection.commit();
      return result;
    }
    else {
      println("Connection was not established.");
      return null;
    }
  }

  def rollback() {
    if (connection != null)
      connection.rollback();
    else
      println("Connection was not established.");
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
