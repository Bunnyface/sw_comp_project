package com.example.playground

import java.sql.{Connection, DriverManager, ResultSet}
import com.typesafe.scalalogging.Logger
import com.typesafe.scalalogging.LazyLogging

/**
 * The client for connecting to the database
 */
class Client extends LazyLogging {
  var connection: Connection = null;
/**
 * Initialize connection to the database
 * @param dbnmame name of the database
 * @param dbuser the username for the database, if none given, environmental variables used
 * @param passwd password for the database, if none given, environmental variables used
 */
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
  /**
 * Execute a database update or query
 * @param query the SQL query string
 */
  def execute(query: String): ResultSet = {
    if (connection != null) {
      val statement = connection.createStatement(
        ResultSet.TYPE_FORWARD_ONLY, 
        ResultSet.CONCUR_UPDATABLE)
      val result = if (query.contains("RETURNING")) {
        statement.executeQuery(query);
      } else {
        statement.executeUpdate(query);
        null
      }
      connection.commit();
      return result;
    }
    else {
      logger.error("Connection was not established.");
      return null;
    }
  }
  /**
 * Execute a database query
 * @param query the SQL query string
 */
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
  /**
 * Execute a sql-rollback in case of database conflicts
 */
  def rollback() {
    if (connection != null) 
      connection.rollback();
    else 
      logger.error("Connection was not established.");
  }
  /**
 * Close the established connection, must be always done at the end of query
 */
  def close() {
    if (connection != null) 
      connection.close();
    else 
      logger.error("Connection was not established.");
  }
  /**
 * Get environmental variables for sql connection
 */
  def getConnectionData(): (String, String, String) = {
    val host = sys.env("PSQLHOST");
    val user = sys.env("PSQLUSER");
    val passwd = sys.env("PSQLPASSWD");
    return (host, user, passwd);
  }
}
