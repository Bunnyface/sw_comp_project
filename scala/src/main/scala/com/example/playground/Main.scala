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

  case class Message(hello: String);

  case class CompareRequest(
    first: String,
    second: String
  );

  case class InsertRequest (
    columns: Array[String],
    data: Array[Array[String]]
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

  def releases: Endpoint[IO, Json] = post("releases") {
    println("Getting releases");
    val response = retrieveFunctions.queryNames();
    Ok(response.asJson);
  }

  def releaseInfo: Endpoint[IO, Json] = post("releases" :: path[String]) { relName: String =>
    var relInfo = retrieveFunctions.queryRelease(relName);
    if (relInfo == null)
      NotFound(new Exception("Release not found"));
    else
      Ok(relInfo.asJson);
  }

  def compare: Endpoint[IO, Json] = post("compare" :: jsonBody[CompareRequest]) { req: CompareRequest =>
    val response = compareFunctions.compare(req.first, req.second);
    if (response != null)
      Ok(response.asJson);
    else
      NotFound(new Exception("One of the requested releases was not found"));
  }

  def insert: Endpoint[IO, Json] = put("insert" :: path[String] :: jsonBody[InsertRequest]) { (table: String, req: InsertRequest) =>
    val response = sendFunctions.queryInsert(table, req.columns, req.data);
    if (response.length != 0)
      Created(response.asJson);
    else
      BadRequest(new Exception("Table not found or data is corrupted"));
  }

  def insertModule: Endpoint[IO, Json] = put("insertModule":: jsonBody[dbmodels.module]) { ( req: dbmodels.module) =>
    val response = sendFunctions.insertModule(req);
    if (response.length != 0)
      Created(response.asJson);
    else
      BadRequest(new Exception("Table not found or data is corrupted"));
  }

  def insertComponent: Endpoint[IO, Json] = put("insertComponent":: jsonBody[dbmodels.component]) { ( req: dbmodels.component) =>
    val response = sendFunctions.insertComponent(req);
    if (response.length != 0)
      Created(response.asJson);
    else
      BadRequest(new Exception("Table not found or data is corrupted"));
  }

  def insertSubComponent: Endpoint[IO, Json] = put("insertSubComponent":: jsonBody[dbmodels.subComponent]) { ( req: dbmodels.subComponent) =>
    val response = sendFunctions.insertSubComponent(req);
    if (response.length != 0)
      Created(response.asJson);
    else
      BadRequest(new Exception("Table not found or data is corrupted"));
  }

  def insertComponentToModule: Endpoint[IO, Json] = put("insertComponentToModule":: jsonBody[dbmodels.componentToModule]) { ( req: dbmodels.componentToModule) =>
    val response = sendFunctions.insertComponentToModel(req);
    if (response.length != 0)
      Created(response.asJson);
    else
      BadRequest(new Exception("Table not found or data is corrupted"));
  }

  def insertSubToComponent: Endpoint[IO, Json] = put("insertSubToComponent":: jsonBody[dbmodels.junction]) { ( req: dbmodels.junction) =>
    val response = sendFunctions.insertSubToComp(req);
    if (response.length != 0)
      Created(response.asJson);
    else
      BadRequest(new Exception("Table not found or data is corrupted"));
  }

  def update: Endpoint[IO, Json] = post("update" :: jsonBody[UpdateRequest]) { req: UpdateRequest =>
    val response =
      sendFunctions.queryUpdate(req.table, req.newValCol, req.newVal, req.condCol, req.condVal);
    if (response != null) {
      Created(response)
    };
    else {
      BadRequest(new Exception("Release not found"));
    };
  }

  val policy: Cors.Policy = Cors.Policy(
    allowsOrigin = _ => Some("*"),
    allowsMethods= _ => Some(Seq("GET", "POST", "PUT")),
    allowsHeaders = headers => Some(headers)
  )

  def service: Service[Request, Response] = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](compare :+: insert :+: update :+: releases :+: releaseInfo :+: insertModule :+: insertComponent :+: insertSubComponent :+: insertComponentToModule :+: insertSubToComponent)
    .toService

  val corsService: Service[Request, Response] = new Cors.HttpFilter(Cors.UnsafePermissivePolicy).andThen(service)
  Await.ready(Http.server.serve(":8081", corsService))
}
