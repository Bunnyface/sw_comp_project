package com.example.playground

import cats.effect.IO
import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.Await
import io.finch._
import io.finch.catsEffect._
import io.finch.circe._
import io.circe.generic.auto._
import client.Client

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

  def service: Service[Request, Response] = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](helloWorld :+: hello)
    .toService

  Await.ready(Http.server.serve(":8081", service))
}