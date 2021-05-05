package com.example.playground

import java.sql.{Connection, DriverManager, ResultSet}
import com.typesafe.scalalogging.Logger
import com.typesafe.scalalogging.LazyLogging


class Client extends LazyLogging {
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
      logger.error("Connection was not established.");
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
      logger.error("Connection was not established.");
      return null;
    }
  }

  def rollback() {
    if (connection != null) 
      connection.rollback();
    else 
      logger.error("Connection was not established.");
  }

  def close() {
    if (connection != null) 
      connection.close();
    else 
      logger.error("Connection was not established.");
  }

  def getConnectionData(): (String, String, String) = {
    val host = sys.env("PSQLHOST");
    val user = sys.env("PSQLUSER");
    val passwd = sys.env("PSQLPASSWD");
    return (host, user, passwd);
  }
}
