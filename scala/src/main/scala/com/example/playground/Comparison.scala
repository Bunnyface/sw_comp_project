package com.example.playground

import scala.collection.mutable.ListBuffer
import client.Client

object compFunction{
  def compareTwoReleases(releaseNameOne: String, releaseNameTwo: String): Map[String, Array[String]] = {
    var firstSet = createSqlQueryForCompare(releaseNameOne);
    var secondSet = createSqlQueryForCompare(releaseNameTwo);

    val sameComponents = getSameComps(firstSet, secondSet);
    val firstExclusive = getExclusiveComps(firstSet, sameComponents);
    val secondExclusive = getExclusiveComps(secondSet, sameComponents);

    return Map("Same components" -> sameComponents.toArray, "Exclusive to first" -> firstExclusive.toArray, "Exclusive to second" -> secondExclusive.toArray);
  }

  def createSqlQueryForCompare(releaseName: String): List[String] = {
    var releaseNameQuotes = "\'" + releaseName + "\'";
    var queryBase = s"""SELECT name, version, cname, cver FROM releases JOIN junctionTable ON releases.name = junctionTable.Releasename JOIN componentTable ON componentTable.cname = junctionTable.componentname WHERE name=$releaseNameQuotes""";

    var sqlClient = new Client();

    sqlClient.connect("defaultdb", "scalauser", "example");

    var resSet = sqlClient.fetch(queryBase);
    var newList = new ListBuffer[String]();
    while(resSet.next()){
      newList += resSet.getString("cname");
    }
    sqlClient.close();
    return newList.toList;

  }

  def getSameComps(first: List[String], second: List[String]): List[String] = {
    var newList = new ListBuffer[String]();

    for (i <- first){
      for (ib <- second){
        if (i == ib){
          newList += i;
        }
      }
    }
    return newList.toList;
  }

  def getExclusiveComps(first: List[String], second: List[String]): List[String] = {
    var newList = new ListBuffer[String]();

    for (i <- first){
      if(!second.contains(i)){
        newList += i;
      }
    }
    return newList.toList;
  }
}