package com.example.playground

import scala.collection.immutable.Stream
import java.sql.{ResultSet}
import java.sql.Types
import io.circe._
import io.circe.syntax._
import com.typesafe.scalalogging.Logger
import com.typesafe.scalalogging.LazyLogging

/**
 * Class for retrieving database objects
 */
object retrieveFunctions extends LazyLogging {

  /*
   * Default connecting function
   * @param client Client to connect
   */
  private lazy val defaultConnecting = (client: Client) => client.connect("defaultdb");

  /*
   * Way to import alternative connecting method.
   */
  var overrideConnecting : Option[Client => Unit] = None

  /*
   * Deciding which connecting method to use.
   */
  def connectClient = overrideConnecting.getOrElse(defaultConnecting)

  /**
   * get all modules from database
   * @return json of all modules
   */
  def queryNames(): Json = {
    val resList = getArray("module", "name");
    return valueToJson(resList.flatten);
  }
  /**
   * Get info on single module
   * @param moduleName name of the module
   * @return json of moduleinfo
   */
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
  /**
   * get all components from database
   * @return json of all components
   */
  def queryComponents(): Json = {
    val fetched = try {
      getMapArray("component", "*");
    } catch {
      case _: Throwable => null;
    }
    return valueToJson(fetched);
  }
  /**
   * get everything from database
   * @return structured json with everything in database
   */
  def retrieveEverything(): Json = {
    val subComp = getMapArray("sub_component");
    val juncTable = getMapArray("junction_table");

    val components = getMapArray("component")
      .map(comp => {
        val related = juncTable.filter(j => j("comp_id") == comp("id"))
          .flatMap(j => j.get("subcomp_id"))
        val subs = subComp.filter(sub => related.contains(sub("id")))
          .map(sub => sub -- Set("id", "row_version"))

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

        (mod -- Set("id", "row_version")) ++
          Map("components" ->
            comps.map(comp =>
              comp ++ additional.filter(j => j("comp_id") == comp("id"))(0) -- Seq("comp_id", "id", "row_version")
            )
          )
      });

    return valueToJson(modules);
  }

  // UTILITY FUNCTIONS

  /**
   * Function to get specific data from database in resultset format
   * @param table table to query from
   * @param columns columns to query
   * @param cond query conditions
   * @return a resultset object of the query
   */
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
    connectClient(sqlClient);

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

  /**
   * Function to get specific data from database in array format
   * @param table table to query from
   * @param columns columns to query
   * @param cond query conditions
   * @return a array object of the query
   */
  def getArray(
                table: String,
                columns: String = "*",
                cond: String = null
              ): Array[Array[Any]] = {
    return resultSetToArray(
      get(table, columns, cond)
    )
  }
  /**
   * Function to get specific data from database in maparray format
   * @param table table to query from
   * @param columns columns to query
   * @param cond query conditions
   * @return a maparray object of the query
   */
  def getMapArray(
                   table: String,
                   columns: String = "*",
                   cond: String = null
                 ): Array[Map[String, Any]] = {
    return resultSetToMapArray(
      get(table, columns, cond)
    )
  }

  /**
   * Convert resultset to array
   * @param result resultset to convert
   * @return array version of resultset
   */
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
  /**
   * Convert resultset to maparray
   * @param result resultset to convert
   * @return maparray version of resultset
   */
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

  /**
   * Converts array to json
   * @param array array to convert
   * @return json version of the array
   */
  def arrayToJson(array: Array[Any]): Json = {
    return array.map(v => valueToJson(v)).asJson
  }
  /**
   * Converts maparray to json
   * @param map maparray to convert
   * @return json version of the maparray
   */
  def mapToJson(map: Map[_, _]): Json = {
    return map.map({ case (k, v) => k.toString() -> valueToJson(v) }).asJson
  }

  /**
   * Convert any type of value to json version
   * @param value the value to convert
   * @return json version of the value
   */
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

  /**
   * Get module id based on name
   * @param name module name
   * @return id of the module
   */
  def getModuleId(name: String): String = {
    val resSet = get("module", "id", f"name = '$name%s'");
    if(resSet.next()) {
      val value = resSet.getString("id");
      return value
    }
    return ""
  }
  /**
   * Get component id based on name
   * @param name component name
   * @return id of the component
   */
  def getCompId(name: String): String = {
    val resSet = get("component", "id", f"name = '$name%s'");
    if(resSet.next()) {
      val value = resSet.getString("id");
      return value
    }
    return ""
  }
  /**
   * Get subcomponent id based on name
   * @param name subcomponent name
   * @return id of the subcomponent
   */
  def getSubId(name: String): String = {
    val resSet = get("sub_component", "id", f"name = '$name%s'");
    if(resSet.next()) {
      val value = resSet.getString("id");
      return value
    }
    return ""
  }
}
