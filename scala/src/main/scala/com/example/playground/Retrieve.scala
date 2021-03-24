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

  def queryRelease(releaseName: String): Map[String, Array[String]]= {
  val releaseInQuotes = s"\'" + releaseName + "\'";
  val versionQuery = s"""SELECT name, version FROM releases WHERE name = $releaseInQuotes""";
  val componentQuery = s"""SELECT componentName FROM junctionTable WHERE releasename=$releaseInQuotes""";

  var sqlClient = new Client();

  sqlClient.connect("defaultdb", "scalauser", "example");

  var resSetVersion = sqlClient.fetch(versionQuery);

  val streamVer = new Iterator[String] {
    def hasNext = resSetVersion.next()
    def next() = resSetVersion.getString(1) + ", " + resSetVersion.getString(2)
  }.toStream

  val releaseInfo = streamVer(0).split(", ");

  var resSetComps = sqlClient.fetch(componentQuery);

  val streamComp = new Iterator[String] {
    def hasNext = resSetComps.next()
    def next() = resSetComps.getString(1)
  }.toStream

  val releaseComponents = streamComp.toArray;

  sqlClient.close();

  return Map("Release info" -> releaseInfo, "Release components" -> releaseComponents);
  }
}
