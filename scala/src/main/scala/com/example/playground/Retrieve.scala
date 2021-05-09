package com.example.playground

import scala.collection.immutable.Stream
import java.sql.{ResultSet}
import java.sql.Types
import io.circe._
import io.circe.syntax._
import com.typesafe.scalalogging.Logger
import com.typesafe.scalalogging.LazyLogging

object retrieveFunctions extends LazyLogging {
  def queryNames(): Json = {
    val resList = getArray("module", "name");
    return valueToJson(resList.flatten);
  }

  def queryRelease(moduleName: String): Json = {
    val fetched = try {
      getMapArray("module", "id, name", f"name='$moduleName%s'")(0);
    } catch {
      case _: Throwable => null;
    }

    if (fetched == null)
      return null;

    val moduleID = fetched.apply("id").asInstanceOf[Int];
    val componentData = getMapArray(
      "module_component as mc, component as c",
      "c.name, c.url, c.version, c.license, c.copyright, mc.usage_type, mc.attr_value1, mc.attr_value2, mc.attr_value3, mc.date, mc.comment_one, mc.comment_two",
      f"mc.module_id=$moduleID%d AND mc.comp_id=c.id"
    );
    return valueToJson(
      fetched ++ Map("components" -> componentData)
    )
  }

  def queryComponents(): Json = {
    val fetched = try {
      getMapArray("component", "*");
    } catch {
      case _: Throwable => null;
    }
    return valueToJson(fetched);
  }

  def retrieveEverything(): Json = {
    val subComp = getMapArray("sub_component");
    val juncTable = getMapArray("junction_table");

    val components = getMapArray("component")
      .map(comp => {
        val related = juncTable.filter(j => j("comp_id") == comp("id"))
          .flatMap(j => j.get("subcomp_id"))
        val subs = subComp.filter(sub => related.contains(sub("id")))
        
        comp ++ Map("sub_components" -> subs)
      });
    
    val modToComp = getMapArray("module_component");
    
    val modules = getMapArray("module")
      .map(mod => {
        val related = modToComp.filter(j => j("module_id") == mod("id"))
          .flatMap(j => j.get("comp_id"))
        val additional = modToComp.filter(j => j("module_id") == mod("id"))
          .map(row => row.-("module_id"))
        val comps = components.filter(comp => related.contains(comp("id")))

        mod ++ 
          Map("components" -> 
            comps.map(comp => 
              comp ++ additional.filter(j => j("comp_id") == comp("id"))(0).-("comp_id")
            )
          )
      });

    return valueToJson(modules);
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
        logger.error(f"Fetching wasn't successful using query '$query%s'")
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

  def mapToJson(map: Map[_, _]): Json = {
    return map.map({ case (k, v) => k.toString() -> valueToJson(v) }).asJson
  }

  def valueToJson(value: Any): Json = {
    value match {
      case value: String => Json.fromString(value)
      case value: Int => Json.fromInt(value)
      case value: Json => value
      case value: Map[_, _] => mapToJson(value)
      case value: Array[Any] => arrayToJson(value)
      case value: Any => Json.fromString(value.toString())
    }
  }

  def getModuleId(name: String): String = {
    val resSet = get("module", "id", f"name = '$name%s'");
    if(resSet.next()) {
      val value = resSet.getString("id");
      return value
    }
    return ""
  }

  def getCompId(name: String): String = {
    val resSet = get("component", "id", f"name = '$name%s'");
    if(resSet.next()) {
      val value = resSet.getString("id");
      return value
    }
    return ""
  }

  def getSubId(name: String): String = {
    val resSet = get("sub_component", "id", f"name = '$name%s'");
    if(resSet.next()) {
      val value = resSet.getString("id");
      return value
    }
    return ""
  }
}
