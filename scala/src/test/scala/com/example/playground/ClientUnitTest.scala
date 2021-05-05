package com.example.playground

import io.finch._
import org.scalatest.FunSuite
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito
import org.mockito.Mockito.when
import org.mockito.Matchers._

import java.sql.{Connection, DriverManager, ResultSet}

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
  val resultSet = mock[ResultSet];
  val mockConnection = mock[Connection];
  print(mockConnection.isInstanceOf[Connection])
  case class mockDriverManager(){
                        def getMockConnection(connection: String, user: String, pwd: String): Connection = {
                          return mockConnection;
                        };
                        }
  val mockDriver = mock[mockDriverManager];

  //accepted
  when(mockDriver.getMockConnection("jdbc:postgresql://THE_HOST/DBNAME","USER","PASSWORD")).thenReturn(mockConnection);
  when(mockDriver.getMockConnection("jdbc:postgresql://THE_HOST/DBNAME","THE_USER","THE_PASSWD")).thenReturn(mockConnection);
  //denied
  /*
  when(
    and(
      mockDriver.getMockConnection(
          not(eq("jdbc:postgresql://THE_HOST/DBNAME")),not(eq("USER")),not(eq("PASSWORD"))
      ),
      mockDriver.getMockConnection(
        not(eq("jdbc:postgresql://THE_HOST/DBNAME")),not(eq("THE_USER")),not(eq("THE_PASSWD"))
      )
    )
  ).thenReturn(null);*/

  /*
  when(mockConnection.createStatement(
    ResultSet.TYPE_FORWARD_ONLY,
    ResultSet.CONCUR_UPDATABLE
  ).executeQuery(query)).then(mockConnection.commit()).thenReturn(resultSet);
  */
  def connectingFunction(connString: String, user: String, passwd: String ) : Connection = {
    print(connString, user, passwd)
    val result = mockDriver.getMockConnection(connString, user, passwd);
    print(result.isInstanceOf[Connection])
    return result;
  }





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
 /*
  test("connect(): Connection is declined when name of the database is not correct"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("RANDOM", null, null, connectingFunction);
    assert(clientToTest.connection == null);
    resetEnv();
  };
  */


  test("execute(): without connection returns null"){
    val clientToTest = new Client;
    assert(clientToTest.execute("This is not a real query") == null)
  }
  /*
  test("execute(): With connection returns ResultSet"){
    fakeEnv();
    val clientToTest = new Client;
    clientToTest.connect("DBNAME", "USER", "PASSWORD", connectingFunction);
    val result = clientToTest.execute("SELECT * FROM modules;");
    assert(result.isInstanceOf[ResultSet]);
    resetEnv();
  }
  test("Nonsense query should fail"){

    assertThrows[org.postgresql.util.PSQLException]{

    }
  }*/


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
    println(one)
    assert(one == "THE_HOST" && two == "THE_USER" && three == "THE_PASSWD")
    resetEnv();
  }




}
