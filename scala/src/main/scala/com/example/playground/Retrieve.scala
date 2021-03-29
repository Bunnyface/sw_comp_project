package com.example.playground

import client.Client
import scala.collection.immutable.Stream
import java.sql.{ResultSet}


object retrieveFunctions{
  def get(
    table: String,
    columns: String = "*",
    cond: String = null
  ): List[IndexedSeq[String]] = {
    val query = if (cond == null) {
      f"SELECT $columns%s FROM $table%s"
    } else {
      f"SELECT $columns%s FROM $table%s WHERE $cond%s"
    }

    val sqlClient = new Client();
    sqlClient.connect("postgres");

    val result = sqlClient.fetch(query);
    val metaData = result.getMetaData();
    val colLength = metaData.getColumnCount();

    val tableValues = new Iterator[IndexedSeq[String]] {
      def hasNext = result.next()
      def next() = {
        for (i <- 1 to colLength)
          yield result.getString(i)
      }
    }.toStream

    return tableValues.toList;
  }

  def queryNames(): List[String] = {
    val resList = get("releases", "name");
    return resList.map(row => row(0));
  }

  def queryRelease(releaseName: String): Map[String, Array[String]]= {
    val versionData = get(
      "releases", "name, version", f"name='$releaseName%s'"
    )(0);

    val componentData = get("junctionTable", "componentName", f"releasename='$releaseName%s'")
      .map(row => row(0));

    return Map(
      "info" -> versionData.toArray, 
      "components" -> componentData.toArray
    );
  }
}
