package com.example.playground

import scala.collection.mutable.ArrayBuffer

object compareFunctions {
  def compare(firstName: String, secondName: String): Map[String, Array[Array[String]]] = {
    val firstSet = getComponents(firstName);
    val secondSet = getComponents(secondName);

    if (firstSet == null || secondSet == null) 
      return null;

    val sameComp = getSame(firstSet, secondSet);

    return Map(
      "same" -> sameComp.toArray, 
      "ex_first" -> getExclusive(firstSet, sameComp).toArray, 
      "ex_second" -> getExclusive(secondSet, sameComp).toArray
    );
  }

  def getComponents(name: String): Array[Array[String]] = {
    val data = retrieveFunctions
      .getArray(
        "module AS m, module_component AS mc, component AS c", 
        "c.name, c.version", 
        f"m.id = mc.module_id AND c.id = mc.comp_id AND m.name='$name%s'")
      .map(row => row.map(v => v.toString()).toArray);
    
    if (data.length > 0)
      return data.toArray;
    
    return Array();
  }

  def getSame(
    first: Array[Array[String]], 
    second: Array[Array[String]]
  ): Array[Array[String]] = {
    return first.filter(row => second.contains(row));
  }

  def getExclusive(
    first: Array[Array[String]], 
    second: Array[Array[String]]
  ): Array[Array[String]] = {
    return first.filter(row => !second.contains(row));
  }
}