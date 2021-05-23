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
import com.typesafe.scalalogging.Logger
import com.typesafe.scalalogging.LazyLogging

import io.circe._
import io.circe.syntax._

import bulkModels._


object Main extends App with LazyLogging {

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
    logger.debug("Getting releases");
    try {
      var retval = retrieveFunctions.queryNames()
      Ok(retval)
    }
    catch {
      case t: Throwable => {
        logger.error(t.toString);
        InternalServerError(new Exception("Problem querying releases"));
      }
    }
  }

  def releaseInfo: Endpoint[IO, Json] = post("releases" :: path[String]) { relName: String =>
    logger.debug(s"Getting releaseinfo for: $relName");
    try {
      var relInfo = retrieveFunctions.queryRelease(relName);
      if (relInfo == null)
        NotFound(new Exception("Release not found"));
      else
        Ok(relInfo);
    }
    catch {
      case t: Throwable => {
        logger.error(t.toString);
        InternalServerError(new Exception(s"Could not query release $relName"));
      }
    }
  }

  def components: Endpoint[IO, Json] = post("components") {
    logger.debug("Getting components");
    try {
      Ok(retrieveFunctions.queryComponents());
    }
    catch {
      case t: Throwable => {
        logger.error(t.toString);
        InternalServerError(new Exception(s"Could not query components"));
      }
    }
  }

  def getEverything: Endpoint[IO, Json] = post("moduleData") {
    logger.debug("Fetching everything");
    try {
      Ok(retrieveFunctions.retrieveEverything());
    }
    catch {
      case t: Throwable => {
        logger.error(t.toString);
        InternalServerError(new Exception(s"Could not query everything"));
      }
    }
  }

  def compare: Endpoint[IO, Json] = post("compare" :: jsonBody[CompareRequest]) { req: CompareRequest =>
    logger.debug("Fetching everything");
    try {
      val response = compareFunctions.compare(req.first, req.second);
      if (response != null)
        Ok(response);
      else
        NotFound(new Exception("One of the requested releases was not found"));
    }
    catch {
      case t: Throwable => {
        logger.error(t.toString);
        InternalServerError(new Exception(s"Could not compare"));
      }
    }
  }

  def insert: Endpoint[IO, Json] = put("insert" :: path[String] :: jsonBody[InsertRequest]) { (table: String, req: InsertRequest) =>
    logger.debug(s"Inserting to $table");
    try {
      val response = sendFunctions.queryInsert(table, req.columns, req.data);
      if (response.length != 0)
        Created(response.asJson);
      else
        BadRequest(new Exception("Table not found or data is corrupted"));
    }
    catch {
      case t: Throwable => {
        logger.error(t.toString);
        InternalServerError(new Exception(s"Could not insert to $table"));
      }
    }
  }


  def insertMany: Endpoint[IO, Json] = put("insertMany" :: jsonBody[Array[Json]]) { reqBody: Array[Json] =>
    logger.debug(s"Inserting multiple");
    try {
      val response = sendFunctions.insertMany(reqBody);
      if (response != null)
        Ok(response);
      else
        BadRequest(new Exception("Table not found or data is corrupted"));
    }
    catch {
      case t: Throwable => {
        logger.error(t.toString);
        InternalServerError(new Exception(s"Failed to insert"));
      }
    }
  }


  def insertModule: Endpoint[IO, Json] = put("insertModule":: jsonBody[dbmodels.module]) { ( req: dbmodels.module) =>
    logger.debug(s"Inserting module");
    try {
      val response = sendFunctions.insertModule(req);
      if (response.length != 0)
        Created(response.asJson);
      else
        BadRequest(new Exception("Table not found or data is corrupted"));
    }
    catch {
      case t: Throwable => {
        logger.error(t.toString);
        InternalServerError(new Exception(s"Failed to insert module"));
      }
    }
  }

  def insertComponent: Endpoint[IO, Json] = put("insertComponent":: jsonBody[dbmodels.component]) { ( req: dbmodels.component) =>
    logger.debug(s"Inserting component");
    try {
      val response = sendFunctions.insertComponent(req);
      if (response.length != 0)
        Created(response.asJson);
      else
        BadRequest(new Exception("Table not found or data is corrupted"));
    }
    catch {
      case t: Throwable => {
        logger.error(t.toString);
        InternalServerError(new Exception(s"Failed to insert component"));
      }
    }
  }

  def insertSubComponent: Endpoint[IO, Json] = put("insertSubComponent":: jsonBody[dbmodels.subComponent]) { ( req: dbmodels.subComponent) =>
    logger.debug(s"Inserting subcomponent");
    try {
      val response = sendFunctions.insertSubComponent(req);
      if (response.length != 0)
        Created(response.asJson);
      else
        BadRequest(new Exception("Table not found or data is corrupted"));
    }
    catch {
      case t: Throwable => {
        logger.error(t.toString);
        InternalServerError(new Exception(s"Failed to insert subcomponent"));
      }
    }
  }

  def insertComponentToModule: Endpoint[IO, Json] = put("insertComponentToModule":: jsonBody[dbmodels.componentToModule]) { ( req: dbmodels.componentToModule) =>
    logger.debug(s"Inserting component to module");
    try {
      val response = sendFunctions.insertComponentToModel(req);
      if (response.length != 0)
        Created(response.asJson);
      else
        BadRequest(new Exception("Table not found or data is corrupted"));
    }
    catch {
      case t: Throwable => {
        logger.error(t.toString);
        InternalServerError(new Exception(s"Failed to insert component to module"));
      }
    }
  }

  def insertSubToComponent: Endpoint[IO, Json] = put("insertSubToComponent":: jsonBody[dbmodels.junction]) { ( req: dbmodels.junction) =>
    logger.debug(s"Inserting subcomponent to component");
    try {
      val response = sendFunctions.insertSubToComp(req);
      if (response.length != 0)
        Created(response.asJson);
      else
        BadRequest(new Exception("Table not found or data is corrupted"));
    }
    catch {
      case t: Throwable => {
        logger.error(t.toString);
        InternalServerError(new Exception(s"Failed to insert subcomponent to component"));
      }
    }
  }

  def update: Endpoint[IO, Json] = post("update" :: jsonBody[UpdateRequest]) { req: UpdateRequest =>
    logger.debug(s"Updating a row");
    try {
      val response =
        sendFunctions.queryUpdate(req.table, req.newValCol, req.newVal, req.condCol, req.condVal);
      if (response != null) {
        Created(response)
      };
      else {
        BadRequest(new Exception("Release not found"));
      };
    }
    catch {
      case t: Throwable => {
        logger.error(t.toString);
        InternalServerError(new Exception(s"Failed to update"));
      }
    }
  }

  def deleteWithId: Endpoint[IO, Json] = delete("delete" :: path[String] :: path[Int]) {
    (table: String, id: Int) =>
      logger.debug(s"Deleting with id: $id");
      try {
        val response = deleteFunctions.deleteById(table, id)
        if (response != null)
          Ok(response)
        else
          BadRequest(new Exception("Element not found"))
      }
      catch {
        case t: Throwable => {
          logger.error(t.toString);
          InternalServerError(new Exception(s"Failed to delete id: $id"));
        }
      }
  }

  def deleteWithName: Endpoint[IO, Json] = delete("delete" :: path[String] :: path[String]) {
    (table: String, identifier: String) =>
      logger.debug(s"Deleting with name: $identifier");
      try {
        val response = deleteFunctions.deleteByName(table, identifier)
        if (response != null)
          Ok(response)
        else
          BadRequest(new Exception("Element not found"))
      }
      catch {
        case t: Throwable => {
          logger.error(t.toString);
          InternalServerError(new Exception(s"Failed to delete name: $identifier"));
        }
      }
  }

  val policy: Cors.Policy = Cors.Policy(
    allowsOrigin = _ => Some("*"),
    allowsMethods= _ => Some(Seq("POST", "PUT")),
    allowsHeaders = headers => Some(headers)
  )

  def service: Service[Request, Response] = Bootstrap
    .serve[Text.Plain](healthcheck)
    .serve[Application.Json](compare :+: insert :+: update :+: releases :+: releaseInfo :+: insertMany
      :+: components :+: insertModule :+: insertComponent :+: insertSubComponent :+: insertComponentToModule 
      :+: insertSubToComponent :+: getEverything :+: deleteWithId :+: deleteWithName)
    .toService

  val corsService: Service[Request, Response] = new Cors.HttpFilter(policy).andThen(service)
  Await.ready(Http.server.serve(":8081", corsService))
}
