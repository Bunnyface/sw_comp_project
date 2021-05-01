package com.example.playground

import scala.collection.immutable.Stream
import java.sql.{ResultSet}
import java.sql.Types
import io.circe._
import io.circe.syntax._

object retrieveFunctions{
  case class Module(
    info: Array[String],
    components: Array[Array[String]]
  );

  def queryNames(): Array[String] = {
    val resList = getArray("module", "name");
    return resList.map(row => row(0).toString());
  }

  def queryRelease(moduleName: String): Module = {
    val fetched = try {
      getArray("module", "*", f"name='$moduleName%s'")(0);
    } catch {
      case _: Throwable => null;
    }

    if (fetched == null)
      return null;

    val moduleID = fetched(0).asInstanceOf[Int];
    val componentData = getArray(
      "module_component as mc, component as c",
      "c.name, c.version",
      f"mc.module_id=$moduleID%d AND mc.comp_id=c.id"
    ).map(row => row.map(v => v.toString()).toArray);

    return Module(
      Array(fetched(1).toString()),
      componentData.toArray
    );
  }

  def retrieveEverything(): Json = {
    val subComp = resultSetToMapArray(get("sub_component"));
    val juncTable = resultSetToMapArray(get("junction_table"));

    val components = resultSetToMapArray(get("component"))
      .map(comp => {
        val related = juncTable.filter(j => j("comp_id") == comp("id"))
          .flatMap(j => j.get("subcomp_id"))
        val subs = subComp.filter(sub => related.contains(sub("id")))
        
        comp ++ Map("sub_components" -> 
          subs.map(c => mapToJson(c)).asJson)
      });
    
    val modToComp = resultSetToMapArray(get("module_component"))
    
    val modules = resultSetToMapArray(get("module"))
      .map(mod => {
        val related = modToComp.filter(j => j("module_id") == mod("id"))
          .flatMap(j => j.get("comp_id"))
        val additional = modToComp.filter(j => j("module_id") == mod("id"))
          .map(row => row.-("module_id"))
        val comps = components.filter(comp => related.contains(comp("id")))

        mod ++ 
          Map("components" -> 
            comps.map(comp => mapToJson(
              comp ++ additional.filter(j => j("comp_id") == comp("id"))(0).-("comp_id")
            ))
          )
      })

    return modules.map(m => mapToJson(m)).asJson;
  }

  // UTILITY FUNCTIONS

  def get(
    table: String,
    columns: String = "*",
    cond: String = null
  ): ResultSet = {
    val query = if (cond == null) {
      f"SELECT $columns%s FROM $table%s"
    } else {
      f"SELECT $columns%s FROM $table%s WHERE $cond%s"
    }

    val sqlClient = new Client();
    sqlClient.connect("defaultdb");

    try {
      val result = sqlClient.fetch(query);
      sqlClient.close()
      return result;
    } catch {
      case _: Throwable => 
        println(f"Fetching wasn't successful using query '$query%s'")
        sqlClient.close()
    }
    return null;
  }

  def getArray(
    table: String,
    columns: String = "*",
    cond: String = null
  ): Array[Array[Any]] = {
    return resultSetToArray(
      get(table, columns, cond)
    )
  }

  def getMapArray(
    table: String,
    columns: String = "*",
    cond: String = null
  ): Array[Map[String, Any]] = {
    return resultSetToMapArray(
      get(table, columns, cond)
    )
  }

  def resultSetToArray(result: ResultSet): Array[Array[Any]] = {
    if (result == null)
      return null
    
    val metadata = result.getMetaData();
    val colLength = metadata.getColumnCount();

    val tableValues = new Iterator[Array[Any]] {
      def hasNext = result.next()
      def next() = {
        for (i <- 1 to colLength)
          yield if (metadata.getColumnType(i) == Types.INTEGER) result.getInt(i)
                else if (metadata.getColumnType(i) == Types.DATE) result.getDate(i)
                else result.getString(i)
      }.toArray
    }.toArray

    return tableValues
  }

  def resultSetToMapArray(result: ResultSet): Array[Map[String, Any]] = {
    if (result == null)
      return null
    
    val metadata = result.getMetaData();
    val colLength = metadata.getColumnCount();

    val tableValues = new Iterator[Map[String, Any]] {
      def hasNext = result.next()
      def next() = {
        for (i <- 1 to colLength)
          yield metadata.getColumnName(i) -> 
                ( if (metadata.getColumnType(i) == Types.INTEGER) result.getInt(i)
                  else if (metadata.getColumnType(i) == Types.DATE) result.getDate(i)
                  else result.getString(i) )
      }.toMap
    }.toArray

    return tableValues
  }

  def arrayToJson(array: Array[Any]): Json = {
    return array.map(v => valueToJson(v)).asJson
  }

  def mapToJson(map: Map[String, Any]): Json = {
    return map.map({ case (k, v) => k -> valueToJson(v) }).asJson
  }

  def valueToJson(value: Any): Json = {
    value match {
      case value: String => Json.fromString(value)
      case value: Int => Json.fromInt(value)
      case value: Json => value
      case value: Array[Any] => value.map(v => valueToJson(v)).asJson
      case value: Any => Json.fromString(value.toString())
    }
  }
}
