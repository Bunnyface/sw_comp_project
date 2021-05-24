package com.example.playground

import io.circe._
import io.circe.syntax._

import retrieveFunctions.{ valueToJson, getMapArray, resultSetToMapArray }

/**
 * Class for deleting database objects
 */
object deleteFunctions {
  val junctionValues = Map(
    "module" -> (Array("module_component"), "module_id"),
    "component" -> (Array("module_component", "junction_table"), "comp_id"),
    "sub_component" -> (Array("junction_table"), "subcomp_id")
  )

  /**
   * Delete object by its id
   * @param table table name to delete from
   * @param id id of object to be deleted
   * @return return the object just deleted
   */
  def deleteById(table: String, id: Int): Json = {
    val client = new Client()
    client.connect("defaultdb")

    try {
      val (tables, columnName) = junctionValues.apply(table)
      val juncQuery = "DELETE FROM %s WHERE %s=%d;"

      for (tableName <- tables) 
        client.execute(juncQuery.format(tableName, columnName, id))
      
      val query = f"DELETE FROM $table%s WHERE id=$id%d RETURNING *;"

      val result = client.execute(query)
      client.close()

      return valueToJson(
        resultSetToMapArray(result)
      )
    } catch { 
      case e: Exception =>
        println(e)
        client.rollback()
        client.close()
        return null
    }
  }
  /**
   * Delete object by its name or name + verison
   * @param table table name to delete from
   * @param id name:version of object to be deleted
   * @return return the object just deleted
   */
  def deleteByName(table: String, identifier: String): Json = {
    val (name, version) = if (identifier.split(":").length == 2) {
      Array(identifier.split(":"))
        .map({ case Array(v1, v2) => (v1, v2)}).head
    } else {
      (identifier, null)
    }

    val elements = if (version == null) {
      getMapArray(table, "id", f"name='$name%s'")
    } else {
      getMapArray(table, "id", f"name='$name%s' AND version='$version%s'")
    }

    if (elements.length != 1)
      return null
    
    return deleteById(table, elements(0).apply("id").asInstanceOf[Int])
  }
}