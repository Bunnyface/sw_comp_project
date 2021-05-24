package com.example.playground

import scala.collection.mutable.ArrayBuffer
import io.circe._
import io.circe.syntax._

import retrieveFunctions.{ valueToJson, getArray }

/**
 * Object for storing functions for comparing modules
 */
object compareFunctions {
  /**
   * Main function that compares to modules
   * @param firstName name of the first module to compare
   * @param secondName name of the first module to compare
   * @return Json with same and exclusive components in modules
   */
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

  /**
   * Util function to get module data
   * @param name name of the module
   * @return array of module data
   */
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

  /**
   * Compare arrays to see same components
   * @param first array of the first components
   * @param second array of the second components
   * @return array of same components
   */
  def getSame(
    first: Array[Array[Any]], 
    second: Array[Array[Any]]
  ): Array[Array[Any]] = {
    return first.filter(row => second.contains(row));
  }

  /**
   * Get compnents that are exclusive to a module
   * @param first array of the first components
   * @param second array of the second components
   * @return array of exclusive components
   */
  def getExclusive(
    first: Array[Array[Any]], 
    second: Array[Array[Any]]
  ): Array[Array[Any]] = {
    return first.filter(row => !second.contains(row));
  }
}