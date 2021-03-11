package com.example.playground

import cats.effect.IO
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

  def releases: Endpoint[IO, Json] = get("releases"){
    var names = retrieveFunctions.queryNames();
    val namesAsJson = names.asJson
    Ok(namesAsJson);
  }
  
  def insert: Endpoint[IO, Int] = get("insert" :: path[String]) { s: String => 
    val response = sendFunctions.queryInsert(s);
    Ok(response);
  }

  def update: Endpoint[IO, Int] = get("update" :: path[String]) { s: String =>
    val response = sendFunctions.queryUpdate(s);
    Ok(response);
  }
  
  def service: Service[Request, Response] = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](helloWorld :+: comparison :+: releases :+: insert :+: update)
    .toService

  Await.ready(Http.server.serve(":8081", service))
}
