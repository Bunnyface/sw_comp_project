package com.example.playground

import client.Client
import scala.collection.immutable.Stream
import java.sql.{ResultSet}



object retrieveFunctions{
  def queryNames(): List[String] = {
  var queryBase = "SELECT name FROM releases";

  var sqlClient = new Client();

  sqlClient.connect("defaultdb", "scalauser", "example");

  var resSet = sqlClient.fetch(queryBase);

  val stream = new Iterator[String] {
    def hasNext = resSet.next()
    def next() = resSet.getString(1)
  }.toStream

  sqlClient.close();
  return stream.toList;
  }
}
