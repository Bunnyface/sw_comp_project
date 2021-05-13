package com.example.playground

import scala.collection.mutable.ArrayBuffer
import io.circe._
import io.circe.syntax._

import retrieveFunctions.{ valueToJson, getArray }

object compareFunctions {
  def compare(firstName: String, secondName: String): Json = {
    val firstSet = getComponents(firstName);
    val secondSet = getComponents(secondName);

    if (firstSet == null || secondSet == null) 
      return null;

    val sameComp = getSame(firstSet, secondSet);

    return valueToJson(
      Map(
        "same" -> sameComp.toArray, 
        "ex_first" -> getExclusive(firstSet, sameComp).toArray, 
        "ex_second" -> getExclusive(secondSet, sameComp).toArray
      )
    )
  }

  def getComponents(name: String): Array[Array[Any]] = {
    val data = getArray(
      "module AS m, module_component AS mc, component AS c", 
      "c.name, c.version", 
      f"m.id = mc.module_id AND c.id = mc.comp_id AND m.name='$name%s'"
    )

    if (data.length > 0)
      return data.toArray;
    
    return Array();
  }

  def getSame(
    first: Array[Array[Any]], 
    second: Array[Array[Any]]
  ): Array[Array[Any]] = {
    return first.filter(row => second.contains(row));
  }

  def getExclusive(
    first: Array[Array[Any]], 
    second: Array[Array[Any]]
  ): Array[Array[Any]] = {
    return first.filter(row => !second.contains(row));
  }
}