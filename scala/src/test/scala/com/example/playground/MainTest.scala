package com.example.playground

import io.finch._
import org.scalatest._
import org.scalatest.FunSuite
import org.scalatest.mockito.MockitoSugar
import org.mockito.Mockito._
import org.mockito.Mockito.when
import org.mockito.Matchers._
import com.typesafe.scalalogging.Logger
import com.typesafe.scalalogging.LazyLogging
import io.circe._
import io.circe.syntax._
import io.circe.Json

import java.sql.{Connection, DriverManager, ResultSet, Statement, ResultSetMetaData}

class MainTest extends FunSuite with MockitoSugar{


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

  //Mocking DriverManager and Connection

  var mockResultSet = mock[ResultSet];
  var mockRsmd = mock[ResultSetMetaData];
  var mockStatement = mock[Statement];
  var mockConnection = mock[Connection];
  case class mockDriverManager(){
                          def getMockConnection(connection: String, user: String, pwd: String): Connection = {
                            return mockConnection;
                          };
                        }
  var mockDriver = mock[mockDriverManager];

  //accepted
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
  //Statement
  when(mockConnection.createStatement(
    ResultSet.TYPE_FORWARD_ONLY,
    ResultSet.CONCUR_UPDATABLE
  )).thenReturn(mockStatement);
  when(mockStatement.executeQuery("SELECT name FROM module")).thenReturn(mockResultSet)

  when(mockConnection.createStatement(
    ResultSet.TYPE_SCROLL_INSENSITIVE,
    ResultSet.CONCUR_READ_ONLY
  )).thenReturn(mockStatement);

  when(mockStatement.executeQuery(
    "SELECT name FROM module;"
  )).thenReturn(mockResultSet)

  when(mockStatement.executeQuery(
    "SELECT name, id FROM module WHERE name=module1;"
  )).thenReturn(mockResultSet)

  def connectingFunction(connString: String, user: String, passwd: String ) : Connection = {
    val result = mockDriver.getMockConnection(connString, user, passwd);
    return result;
  }


  fakeEnv();
  retrieveFunctions.overrideConnecting = Some((client: Client) => client.connect("DBNAME", "USER", "PASSWORD", connectingFunction));
  test("releases: POST: /releases should return names of the releases from the db."){
    //Case specific mocking
    when(mockResultSet.getMetaData()).thenReturn(mockRsmd);
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getString(1)).thenReturn("module1");
    when(mockRsmd.getColumnType(1)).thenReturn(12) //columntype VARCHAR
    when(mockRsmd.getColumnCount()).thenReturn(1);

    val response = Main.releases(Input.post("/releases")).awaitValue()

    val value : Either[io.circe.DecodingFailure, Array[String]] = response match{
      case None => fail("Response was None")
      case Some(v: Any) => {
        v match {
          case Left => fail("The response was error")
          case Right(json : Json) => json.as[Array[String]]
        }
      }
    }
    value match {
      case Right(arr: Array[String]) => {
      println("Response to /releases: " + arr(0))
      assert(arr.isInstanceOf[Array[String]])}
    }
  }
  /* This implementation is incomplete. It works as an integration test, but needs more mocking to work as a unit test.
  retrieveFunctions.overrideConnecting = Some((client: Client) => client.connect("DBNAME", "USER", "PASSWORD", connectingFunction));
  test("releaseInfo: POST: /release/{releasename} should return data"){
    //retrieveFunctions.overrideConnecting = None;

    when(mockResultSet.getMetaData()).thenReturn(mockRsmd);
    when(mockResultSet.next()).thenReturn(true).thenReturn(false);
    when(mockResultSet.getString(1)).thenReturn("1");
    when(mockResultSet.getString(2)).thenReturn("module1");
    when(mockRsmd.getColumnType(1)).thenReturn(4) //columntype INTEGER
    when(mockRsmd.getColumnType(2)).thenReturn(12) //columntype VARCHAR
    when(mockRsmd.getColumnCount()).thenReturn(3);

    val response = Main.releaseInfo(Input.post("/releases/module1")).awaitValue()

    val value : Unit =
      response match{
      case None => fail("Response was None")
      case Some(v: Any) => {
        v match {
          case Left => fail("The response was error")
          case Right(json : Json) => assert(json.isInstanceOf[Json])
        }
      }
    }
  }*/
  resetEnv();


  test("GET: healthcheck/ should return OK") {
    assert(Main.healthcheck(Input.get("/")).awaitValue() == Some(Right("OK")))
  }

  test("GET: trying all other endpoints should fail"){
    info("/releases")
    assert(Main.releases(Input.get("/releases")).awaitValue() == None)
    info("/releaseInfo")
    assert(Main.releaseInfo(Input.get("/releaseInfo")).awaitValue() == None)
    info("/components")
    assert(Main.components(Input.get("/components")).awaitValue() == None)
    info("/getEverything")
    assert(Main.getEverything(Input.get("/getEverything")).awaitValue() == None)
    info("/compare")
    assert(Main.compare(Input.get("/compare")).awaitValue() == None)
    info("/insert")
    assert(Main.insert(Input.get("/insert")).awaitValue() == None)
    info("/insertMany")
    assert(Main.insertMany(Input.get("/insertMany")).awaitValue() == None)
    info("/insertModule")
    assert(Main.insertModule(Input.get("/insertModule")).awaitValue() == None)
    info("/insertComponent")
    assert(Main.insertComponent(Input.get("/insertComponent")).awaitValue() == None)
    info("/insertSubComponent")
    assert(Main.insertSubComponent(Input.get("/insertSubComponent")).awaitValue() == None)
    info("/insertComponentToModule")
    assert(Main.insertComponentToModule(Input.get("/insertComponentToModule")).awaitValue() == None)
    info("/insertSubToComponent")
    assert(Main.insertSubToComponent(Input.get("/insertSubToComponent")).awaitValue() == None)
    info("/update")
    assert(Main.update(Input.get("/update")).awaitValue() == None)
    info("/deleteWithId")
    assert(Main.deleteWithId(Input.get("/deleteWithId")).awaitValue() == None)
    info("/deleteWithName")
    assert(Main.deleteWithName(Input.get("/deleteWithName")).awaitValue() == None)
  }


}
