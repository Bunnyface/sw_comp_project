package com.example.playground

import java.sql.{Connection, DriverManager, ResultSet}
import com.typesafe.scalalogging.Logger
import com.typesafe.scalalogging.LazyLogging
import java.lang.NullPointerException;
import org.postgresql.util.PSQLException;

/**
 * The client for connecting to the database
 */
class Client extends LazyLogging {
  var connection: Connection = null;

  private[this] def defaultConnecting(connString: String, user: String, passwd: String ) : Connection = {
    val result = DriverManager.getConnection(connString, user, passwd);
    return result;
  }

  /**
   * Initialize connection to the database
   * @param dbnmame name of the database
   * @param dbuser the username for the database, if none given, environmental variables used
   * @param passwd password for the database, if none given, environmental variables used
   * @param connectFunction lets tests mock DriverManager to fake database connection
   */

  //connectFunction can be changed for tests
  def connect(dbname: String, dbuser: String = null, passwd: String = null, connectFunction: (String, String, String) => Connection = defaultConnecting) {
    //classOf[org.postgresql.Driver];
    try{
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
    catch{
      case ex: NullPointerException => {
        logger.error(
          "Connection was not established."
        )
      }
      case ex: PSQLException => {
        logger.error(
          "Database threw an error while trying to connect. Connection was not established."
        )
      }
      connection = null;
    }
  }
  /**
 * Execute a database update or query
 * @param query the SQL query string
 */
  def execute(query: String): ResultSet = {
    try{
      if (connection != null && query != null) {
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
        if(connection == null){
          logger.error("Connection has not been established.");
        }
        if(query == null){
          logger.error("Execute failed due to null query.");
        }
        return null;
      }
    }
    catch{
      case ex: PSQLException => {
        logger.error("Database threw an error while executing query.")
        return null;
      }
    }

  }
  /**
 * Execute a database query
 * @param query the SQL query string
 */
  def fetch(query: String): ResultSet = {
    try{
      if (connection != null) {
        val result = connection.createStatement(
          ResultSet.TYPE_SCROLL_INSENSITIVE,
          ResultSet.CONCUR_READ_ONLY
        ).executeQuery(query);
        connection.commit();
        return result;
      }
      else {
        logger.error("Connection has not been established.");
        return null;
      }
    }
    catch{
      case ex: PSQLException => {
        logger.error("Database threw an error while executing query.")
        return null;
      }
    }
  }
  /**
 * Execute a sql-rollback in case of database conflicts
 */
  def rollback() {
    if (connection != null)
      connection.rollback();
    else
      logger.error("Connection has not been established.");
  }
  /**
 * Close the established connection, must be always done at the end of query
 */
  def close() {
    if (connection != null)
      connection.close();
    else
      logger.error("There is no connection to close.");
  }

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
