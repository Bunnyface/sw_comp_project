package com.example.playground

import cats.effect.IO
import com.twitter.finagle.http.filter.Cors
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await
import io.finch._
import io.finch.catsEffect._
import io.finch.circe._
import io.circe.generic.auto._
import spray.json._
import DefaultJsonProtocol._
import client.Client

import io.circe._
import io.circe.syntax._

object Main extends App {

  case class Message(hello: String)

  def healthcheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }

  def helloWorld: Endpoint[IO, String] = get("hello") {
    Ok("Hello, World!");
  }

  def hello: Endpoint[IO, String] = get("hello" :: path[String]) { s: String =>
    val client = new Client();

    // Set up a PostgreSQL db with user default_user and password 123.
    client.connect("testdb", "default_user", "123");

    // Create a "users" table with id and name columns for this to work.
    // Id column has to be SERIAL (autoincrement type).
    var userData = client.fetch(f"SELECT * FROM users WHERE name='$s%s';");

    if (!userData.next()) {
      client.execute(f"INSERT INTO users (name) VALUES ('$s%s');");
      userData = client.fetch(f"SELECT * FROM users WHERE name='$s%s';");
    }
    else
      userData.previous();
    client.close();

    if (userData.next()) {
      val userId = userData.getInt("id");
      Ok(f"Hello, $s%s! Your id is: $userId%d");
    }
    else
      Ok("Your id wasn't found.");
  }

  def comparison: Endpoint[IO, spray.json.JsValue] = get("comparison" :: path[String]) { s: String =>
    var releases = s.split(":")
    val theMap = compFunction.compareTwoReleases(releases(0), releases(1))
    var start = "{"
    var end = "}"
    var beef = ""
    for ((k,v) <- theMap) {
      beef += "\"" +  k + "\"" + ":[\"" + v.mkString("\",\"") + "\"],";
    }
    val jsonString = start + beef.substring(0, beef.length - 1) + end;
    Ok(jsonString.parseJson);
  }

  def releases: Endpoint[IO,Json] = get("releases"){
    var names = retrieveFunctions.queryNames();
    val namesAsJson = names.asJson;
    Ok(namesAsJson);
  }
/*
  def insert: Endpoint[IO, Int] = get("insert" :: path[String]) { s: String =>
    val response = sendFunctions.insert(s);
    Ok(response);
  }

  def update: Endpoint[IO, Int] = get("update" :: path[String]){ s: String =>
    val response = sendFunctions.update(s);
    Ok(response);
  }
*/

  def releaseInfo: Endpoint[IO, Json] = get("releases" :: path[String]){ relName: String =>
    var relInfo = retrieveFunctions.queryRelease(relName);
    val relInfoAsJson = relInfo.asJson;
    Ok(relInfoAsJson);
  }

  val policy: Cors.Policy = Cors.Policy(
    allowsOrigin = _ => Some("*"),
    allowsMethods= _ => Some(Seq("GET", "POST", "PUT")),
    allowsHeaders = headers => Some(headers)
  )

  def service: Service[Request, Response] = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](helloWorld :+: hello :+: comparison :+: releases :+: releaseInfo)
    .toService

  val corsService: Service[Request, Response] = new Cors.HttpFilter(Cors.UnsafePermissivePolicy).andThen(service)
  Await.ready(Http.server.serve(":8081", corsService))
}
