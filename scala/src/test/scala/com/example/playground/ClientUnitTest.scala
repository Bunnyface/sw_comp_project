package com.example.playground

import io.finch._
import org.scalatest.FunSuite
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.mockito.Matchers._

<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
import java.sql.{Connection, DriverManager, ResultSet, Statement}

=======
import java.sql.{Connection, DriverManager, ResultSet}
>>>>>>> First Unit tests for Client
=======
import java.sql.{Connection, DriverManager, ResultSet}
>>>>>>> First Unit tests for Client
=======
import java.sql.{Connection, DriverManager, ResultSet, Statement}

>>>>>>> More testing on Client

class ClientUnitTest extends FunSuite with MockitoSugar{

  //Setup for handling environment variables

  def setEnv(key: String, value: String) = {
    val field = System.getenv().getClass().getDeclaredField("m");
    field.setAccessible(true);
    val map = field.get(System.getenv()).asInstanceOf[java.util.Map[java.lang.String, java.lang.String]]
    map.put(key,value)
  }

  val originalEnvHost = sys.env("PSQLHOST");
  val originalEnvUser = sys.env("PSQLUSER");
  val originalEnvPasswd = sys.env("PSQLPASSWD");

  def resetEnv(): Unit = {
    setEnv("PSQLHOST", originalEnvHost);
    setEnv("PSQLUSER", originalEnvUser);
    setEnv("PSQLPASSWD", originalEnvPasswd);
  }

  def fakeEnv(): Unit = {
    //Setting environment variables for test.
    setEnv("PSQLHOST","THE_HOST");
    setEnv("PSQLUSER","THE_USER");
    setEnv("PSQLPASSWD","THE_PASSWD");
  }

  //Database mocking for client

  //In memory as if "db"
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
  val mockResultSet = mock[ResultSet];
  val mockStatement = mock[Statement];
  val mockConnection = mock[Connection];
  case class mockDriverManager(){
                          def getMockConnection(connection: String, user: String, pwd: String): Connection = {
                            return mockConnection;
                          };
=======
=======
>>>>>>> First Unit tests for Client
  val resultSet = mock[ResultSet];
=======
  val mockResultSet = mock[ResultSet];
  val mockStatement = mock[Statement];
>>>>>>> More testing on Client
  val mockConnection = mock[Connection];
  case class mockDriverManager(){
<<<<<<< HEAD
                        def getMockConnection(connection: String, user: String, pwd: String): Connection = {
                          return mockConnection;
                        };
<<<<<<< HEAD
>>>>>>> First Unit tests for Client
=======
>>>>>>> First Unit tests for Client
=======
                          def getMockConnection(connection: String, user: String, pwd: String): Connection = {
                            return mockConnection;
                          };
>>>>>>> More testing on Client
                        }
  val mockDriver = mock[mockDriverManager];

  //accepted
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
  when(
    mockDriver.getMockConnection(
      "jdbc:postgresql://THE_HOST/DBNAME","USER","PASSWORD"
    )
  ).thenReturn(mockConnection);
  when(
    mockDriver.getMockConnection(
      "jdbc:postgresql://THE_HOST/DBNAME","THE_USER","THE_PASSWD"
    )
  ).thenReturn(mockConnection);

  when(mockConnection.createStatement(
    ResultSet.TYPE_FORWARD_ONLY,
    ResultSet.CONCUR_UPDATABLE
  )).thenReturn(mockStatement);
  when(mockStatement.executeQuery("INSERT INTO modules  VALUES time, date, num, val RETURNING *;")).thenReturn(mockResultSet)

  when(mockConnection.createStatement(
    ResultSet.TYPE_SCROLL_INSENSITIVE,
    ResultSet.CONCUR_READ_ONLY
  )).thenReturn(mockStatement);
  when(mockStatement.executeQuery("SELECT * FROM modules;")).thenReturn(mockResultSet)

  def connectingFunction(connString: String, user: String, passwd: String ) : Connection = {
    val result = mockDriver.getMockConnection(connString, user, passwd);
=======
=======
>>>>>>> First Unit tests for Client
  when(mockDriver.getMockConnection("jdbc:postgresql://THE_HOST/DBNAME","USER","PASSWORD")).thenReturn(mockConnection);
  when(mockDriver.getMockConnection("jdbc:postgresql://THE_HOST/DBNAME","THE_USER","THE_PASSWD")).thenReturn(mockConnection);
  //denied
  /*
=======
>>>>>>> More testing on Client
  when(
    mockDriver.getMockConnection(
      "jdbc:postgresql://THE_HOST/DBNAME","USER","PASSWORD"
    )
  ).thenReturn(mockConnection);
  when(
    mockDriver.getMockConnection(
      "jdbc:postgresql://THE_HOST/DBNAME","THE_USER","THE_PASSWD"
    )
  ).thenReturn(mockConnection);

  when(mockConnection.createStatement(
    ResultSet.TYPE_FORWARD_ONLY,
    ResultSet.CONCUR_UPDATABLE
  )).thenReturn(mockStatement);
  when(mockStatement.executeQuery("INSERT INTO modules  VALUES time, date, num, val RETURNING *;")).thenReturn(mockResultSet)

  when(mockConnection.createStatement(
    ResultSet.TYPE_SCROLL_INSENSITIVE,
    ResultSet.CONCUR_READ_ONLY
  )).thenReturn(mockStatement);
  when(mockStatement.executeQuery("SELECT * FROM modules;")).thenReturn(mockResultSet)

  def connectingFunction(connString: String, user: String, passwd: String ) : Connection = {
    val result = mockDriver.getMockConnection(connString, user, passwd);
<<<<<<< HEAD
    print(result.isInstanceOf[Connection])
<<<<<<< HEAD
>>>>>>> First Unit tests for Client
=======
>>>>>>> First Unit tests for Client
=======
>>>>>>> More testing on Client
    return result;
  }



<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======


>>>>>>> First Unit tests for Client
=======


>>>>>>> First Unit tests for Client
=======
>>>>>>> More testing on Client
  //Tests:

  test("connect(): Connection is established with the mock db with provided credentials."){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    assert(clientToTest.connection.isInstanceOf[Connection])
    resetEnv();
  }

  test("connect(): Connection is established with the mock db without credentials."){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", null, null, connectingFunction);
    assert(clientToTest.connection.isInstanceOf[Connection]);
    resetEnv();
  }
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD

=======
 /*
>>>>>>> First Unit tests for Client
=======
 /*
>>>>>>> First Unit tests for Client
=======

>>>>>>> More testing on Client
  test("connect(): Connection is declined when name of the database is not correct"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("RANDOM", null, null, connectingFunction);
    assert(clientToTest.connection == null);
    resetEnv();
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
  }


  test("execute(): Without connection returns null"){
    val clientToTest = new Client;
    assert(clientToTest.execute("This is not a real query") == null)
  }

  test("execute(): With connection returns ResultSet when query contains RETURNING"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.execute("INSERT INTO modules  VALUES time, date, num, val RETURNING *;");
    assert(result.isInstanceOf[ResultSet]);
    resetEnv();
  }

  test("execute(): String that is not query should fail"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.execute("Nonsense query");
    assert(result == null);
    resetEnv();

  }

  test("execute(): Querying null should fail"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.execute(null);
    assert(result == null);
    resetEnv();
  }

  test("execute(): Querying empty string should fail"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.execute("");
    assert(result == null);
    resetEnv();
  }

  test("fetch(): Without connection returns null"){
    val clientToTest = new Client;
    assert(clientToTest.fetch("This is not a real query") == null)
  }

  test("fetch(): With connection returns ResultSet"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.fetch("SELECT * FROM modules;");
    assert(result.isInstanceOf[ResultSet]);
    resetEnv();
  }

  test("fetch(): String that is not query should fail"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.fetch("Nonsense query");
    assert(result == null);
    resetEnv();

  }

  test("fetch(): Querying null should fail"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.fetch(null);
    assert(result == null);
    resetEnv();
  }

  test("fetch(): Querying empty string should fail"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.fetch("");
    assert(result == null);
    resetEnv();
  }

  test("rollback(): Without connection returns null"){
    val clientToTest = new Client;
    assert(clientToTest.rollback() == null)
  }

  test("rollback(): Should rollback set query"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.fetch("SELECT * FROM modules;");
    assert(result.isInstanceOf[ResultSet]);
    resetEnv();
  }


  test("close(): Without connection returns null."){
    val clientToTest = new Client;
    assert(clientToTest.connection == null)
  }

  test("lol"){
    val clientToTest = new Client;
    println(clientToTest.connection)
    clientToTest.connect("defaultdb")
    println(clientToTest.connection)
    println(clientToTest.connection.isClosed())
    clientToTest.close()
    println(clientToTest.connection)
    println(clientToTest.connection.isClosed())
  }
  test("close(): Should close existing connection"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    print(clientToTest.connection.isClosed())
    if(!clientToTest.connection.isClosed()){
      clientToTest.close()
      assert(clientToTest.connection.isClosed());
    }
    else{
      fail("Client didn't get a connection.")
    }
    resetEnv();
  }
=======
=======
>>>>>>> First Unit tests for Client
  };
  */
=======
  }
>>>>>>> More testing on Client


  test("execute(): Without connection returns null"){
    val clientToTest = new Client;
    assert(clientToTest.execute("This is not a real query") == null)
  }

  test("execute(): With connection returns ResultSet when query contains RETURNING"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.execute("INSERT INTO modules  VALUES time, date, num, val RETURNING *;");
    assert(result.isInstanceOf[ResultSet]);
    resetEnv();
  }

  test("execute(): String that is not query should fail"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.execute("Nonsense query");
    assert(result == null);
    resetEnv();

  }

  test("execute(): Querying null should fail"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.execute(null);
    assert(result == null);
    resetEnv();
  }

  test("execute(): Querying empty string should fail"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.execute("");
    assert(result == null);
    resetEnv();
  }

  test("fetch(): Without connection returns null"){
    val clientToTest = new Client;
    assert(clientToTest.fetch("This is not a real query") == null)
  }

  test("fetch(): With connection returns ResultSet"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.fetch("SELECT * FROM modules;");
    assert(result.isInstanceOf[ResultSet]);
    resetEnv();
  }

  test("fetch(): String that is not query should fail"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.fetch("Nonsense query");
    assert(result == null);
    resetEnv();

  }

  test("fetch(): Querying null should fail"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.fetch(null);
    assert(result == null);
    resetEnv();
  }

  test("fetch(): Querying empty string should fail"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.fetch("");
    assert(result == null);
    resetEnv();
  }

  test("rollback(): Without connection returns null"){
    val clientToTest = new Client;
    assert(clientToTest.rollback() == null)
  }

  test("rollback(): Should rollback set query"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.fetch("SELECT * FROM modules;");
    assert(result.isInstanceOf[ResultSet]);
    resetEnv();
  }


<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
>>>>>>> First Unit tests for Client
=======
>>>>>>> First Unit tests for Client
=======
  test("close(): Without connection logs error"){
    val clientToTest = new Client;
    assert(clientToTest.connection == null)
  }
  */
>>>>>>> More testing on Client
=======
  test("close(): Without connection returns null."){
    val clientToTest = new Client;
    assert(clientToTest.connection == null)
  }

  test("lol"){
    val clientToTest = new Client;
    println(clientToTest.connection)
    clientToTest.connect("defaultdb")
    println(clientToTest.connection)
    println(clientToTest.connection.isClosed())
    clientToTest.close()
    println(clientToTest.connection)
    println(clientToTest.connection.isClosed())
  }
  test("close(): Should close existing connection"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    print(clientToTest.connection.isClosed())
    if(!clientToTest.connection.isClosed()){
      clientToTest.close()
      assert(clientToTest.connection.isClosed());
    }
    else{
      fail("Client didn't get a connection.")
    }
    resetEnv();
  }
>>>>>>> Commit before rebasing.

  test("getConnectionData(): Should return set of strings"){
    fakeEnv();
    val clientToTest = new Client;
    val (one, two, three) = clientToTest.getConnectionData()
    assert(one.isInstanceOf[String] && two.isInstanceOf[String] && three.isInstanceOf[String])
    resetEnv();
  }

  test("getConnectionData(): Should return values of environment variables"){
    fakeEnv();
    val clientToTest = new Client;
    val (one, two, three) = clientToTest.getConnectionData()
<<<<<<< HEAD
<<<<<<< HEAD
<<<<<<< HEAD
=======
    println(one)
>>>>>>> First Unit tests for Client
=======
    println(one)
>>>>>>> First Unit tests for Client
=======
>>>>>>> More testing on Client
    assert(one == "THE_HOST" && two == "THE_USER" && three == "THE_PASSWD")
    resetEnv();
  }




}
