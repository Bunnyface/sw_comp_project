package client

import java.sql.{Connection, DriverManager, ResultSet}

class Client {
  var connection: Connection = null;

  def connect(dbname: String, dbuser: String, passwd: String) {
    classOf[org.postgresql.Driver];
    val connString = f"jdbc:postgresql://localhost:5432/$dbname%s";
    connection = DriverManager.getConnection(connString, dbuser, passwd);
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
    if (connection != null) {
      connection.close();
    }
    else {
      println("Connection was not established.");
    }
  }
}
