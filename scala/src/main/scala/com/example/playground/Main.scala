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

import io.circe._
import io.circe.syntax._



object Main extends App {

  case class Message(hello: String)

  case class InsertRequest(
    table: String, 
    data: Array[String]
  );

  case class UpdateRequest(
    table: String, 
    newValCol: String, 
    newVal: String, 
    condCol: String, 
    condVal: String
  );

  def healthcheck: Endpoint[IO, String] = get(pathEmpty) {
    Ok("OK")
  }

  def compare: Endpoint[IO, Json] = get("compareReleases" :: path[String]) { s: String =>
    println("GOT HERE");
    var releases = s.split(":")
    val theMap = compFunction.compareTwoReleases(releases(0), releases(1))
    Ok(theMap.asJson);
  }

  def releases: Endpoint[IO,Json] = get("releases"){
    var names = retrieveFunctions.queryNames();
    Ok(names.asJson);
  }
  
  def insert: Endpoint[IO, Json] = post("insert" :: jsonBody[InsertRequest]) { req: InsertRequest => 
    val response = sendFunctions.queryInsert(req.table, req.data);
    if (response != null)
      Created(response.asJson);
    else
      BadRequest(new Exception("Table not found or data is corrupted"));
  }

  def update: Endpoint[IO, Json] = post("update" :: jsonBody[UpdateRequest]) { req: UpdateRequest =>
    val response = 
      sendFunctions.queryUpdate(req.table, req.newValCol, req.newVal, req.condCol, req.condVal);
    if (response != null)
      Created(response.asJson);
    else
      BadRequest(new Exception("Release not found"));
  }

  def releaseInfo: Endpoint[IO, Json] = get("releases" :: path[String]){ relName: String =>
    var relInfo = retrieveFunctions.queryRelease(relName);
    if (relInfo == null)
      NotFound(new Exception("Release not found"));
    else
      Ok(relInfo.asJson);
  }

  val policy: Cors.Policy = Cors.Policy(
    allowsOrigin = _ => Some("*"),
    allowsMethods= _ => Some(Seq("GET", "POST", "PUT")),
    allowsHeaders = headers => Some(headers)
  )

  def service: Service[Request, Response] = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](compare :+: insert :+: update :+: releases :+: releaseInfo)
    .toService

  val corsService: Service[Request, Response] = new Cors.HttpFilter(Cors.UnsafePermissivePolicy).andThen(service)
  Await.ready(Http.server.serve(":8081", corsService))
}
