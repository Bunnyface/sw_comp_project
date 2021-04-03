package com.example.playground

import scala.collection.mutable.ListBuffer

object compareFunctions {
  def compare(firstName: String, secondName: String): Map[String, Array[String]] = {
    val firstSet = getComponents(firstName);
    val secondSet = getComponents(secondName);

    if (firstSet == null || secondSet == null) 
      return null;

    val sameComp = getSame(firstSet, secondSet);

    return Map(
      "Same components" -> sameComp.toArray, 
      "Exclusive to first" -> getExclusive(firstSet, sameComp).toArray, 
      "Exclusive to second" -> getExclusive(secondSet, sameComp).toArray
    );
  }

  def getComponents(name: String): List[String] = {
    val data = retrieveFunctions
      .get(
        "releases AS r, junctionTable AS jt, componentTable AS ct", 
        "cname", 
        f"r.name = jt.releasename AND ct.cname=jt.componentname AND name='$name%s'")
      .map(row => row(0));
    return data.toList;
  }

  def getSame(first: List[String], second: List[String]): List[String] = {
    return first.filter(row => second.contains(row));
  }

  def getExclusive(first: List[String], second: List[String]): List[String] = {
    return first.filter(row => !second.contains(row));
  }
}