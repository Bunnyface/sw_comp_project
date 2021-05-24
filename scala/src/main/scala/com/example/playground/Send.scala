package com.example.playground

import io.circe._
import io.circe.syntax._
import io.circe.generic.auto._
import retrieveFunctions._
import bulkModels._

import com.typesafe.scalalogging.Logger
import com.typesafe.scalalogging.LazyLogging

/**
 * Class for inserting data to database
 */
object sendFunctions extends LazyLogging {
  /**
   * Insert anything to database
   * @param table table to insert to
   * @param columns columns to insert
   * @param data data to insert
   * @return array of inserted data
   */
  def insert(
    table: String,
    columns: Array[String],
    data: Array[Array[Any]]
  ): Array[Map[String, Any]] = {
    if (data == null || data.length == 0)
      return Array();

    val sqlClient = new Client();
    sqlClient.connect("defaultdb");

    val columnString = columns.mkString(", ");

    val result: Array[Map[String, Any]] = data.map(array => {
      val row = array.map(v => getValueFormat(v)).mkString(", ");
      val query = f"INSERT INTO $table%s ($columnString%s) VALUES ($row%s) RETURNING *;";

      println(query)
      try {
        resultSetToMapArray(
          sqlClient.execute(query)
        )(0)
      } catch {
        case _: Throwable =>
          logger.debug("Insert wasn't successful");
          null;
      }
    }).filter(row => row != null);

    sqlClient.close();
    return result;
  }

  /**
   * Simplified version of insert for specific insertions
   * @param table table to insert to
   * @param columns columns to insert
   * @param data data to insert
   * @return array of inserted data in jsons
   */
  def queryInsert(
                   table: String,
                   columns: Array[String],
                   data: Array[Array[String]]
                 ): Array[Json] = {
    logger.debug("INSERTING")
    return insert(
      table, 
      columns, 
      data.map(row => row.map(v => v.asInstanceOf[Any]))
    ).map(v => valueToJson(v));
  }

  /**
   * Update database object
   * @param table table name to update
   * @param newValCol columns of new values
   * @param newVal new values
   * @param condCol columns which conditions match
   * @param condVal conditions of matching
   * @return json of updated data
   */
  def queryUpdate(
                   table: String,
                   newValCol: String,
                   newVal: String,
                   condCol: String,
                   condVal: String
                 ): Json = {
    val sqlClient = new Client();
    sqlClient.connect("defaultdb");
    logger.debug("Sending update query");

    val change = f"${newValCol}='${newVal}'";
    val condition = f"${condCol}='${condVal}'";
    val query = f"UPDATE $table%s SET $change%s, row_version=row_version+1 WHERE $condition%s RETURNING *;";

    val version = getArray(table, "row_version", condition)(0)(0).asInstanceOf[Int];

    try {
      val returnedRow = resultSetToMapArray(
        sqlClient.execute(query)
      )(0);
      val updatedVersion = returnedRow.apply("row_version").asInstanceOf[Int];
      if (updatedVersion - version != 1) {
        sqlClient.rollback();
        throw new Exception("Parallel update occurred, performing the rollback..");
      }
      sqlClient.close();
      return valueToJson(returnedRow);
    } catch {
      case e: Exception =>
        println(e)
        logger.error("Update wasn't successful")
    }
    sqlClient.close();
    logger.debug("RETURNING NULL");
    return null;
  }

  /**
   * Insert a module to database
   * @param mod module to insert
   * @return array of json inserted
   */
  def insertModule(mod: dbmodels.module): Array[Json]= {
    logger.debug("Insert Module")
    val res = queryInsert(
      "module",
      Array[String]{"name"},
      Array[Array[String]]{Array[String]{mod.name}}
    )
    return res
  }

  /**
   * insert a component to database
   * @param comp component to insert
   * @return array of json inserted
   */
  def insertComponent(comp: dbmodels.component): Array[Json]= {
    logger.debug("Insert Component")
    val res = queryInsert(
      "component",
      Array[String]("name", "url", "version", "license", "copyright"),
      Array[Array[String]](Array[String](comp.name, comp.url, comp.version, comp.license, comp.copyright))
    )
    return res
  }

  /**
   * insert a subcomponent to database
   * @param comp subcomponent to insert
   * @return array of json inserted
   */
  def insertSubComponent(comp: dbmodels.subComponent): Array[Json]= {
    logger.debug("Insert subComponent")
    val res = queryInsert(
      "sub_component",
      Array[String]("name", "url", "version", "license", "copyright"),
      Array[Array[String]](Array[String](comp.name, comp.url, comp.version, comp.license, comp.copyright))
    )
    return res
  }

  /**
   * Insert component with relation to module to database
   * @param comp component to insert
   * @return array of json inserted
   */
  def insertComponentToModel(comp: dbmodels.componentToModule): Array[Json]= {
    logger.debug("Insert ComponentToModel")
    val mod_id = getModuleId(comp.modulename)
    val comp_id = getCompId(comp.componentname)
    val res = queryInsert(
      "module_component",
      Array[String](
        "module_id",
        "comp_id",
        "usage_type",
        "attr_value1",
        "attr_value2",
        "attr_value3",
        "date",
        "comment_one",
        "comment_two"),
      Array[Array[String]](Array[String](
        mod_id,
        comp_id,
        comp.usage_type,
        comp.attr_value1,
        comp.attr_value2,
        comp.attr_value3,
        comp.date,
        comp.comment_one,
        comp.comment_two))
    )
    return res
  }

  /**
   * Insert subcomponent with relation to component to database
   * @param comp subcomponent to insert
   * @return array of json inserted
   */
  def insertSubToComp(comp: dbmodels.junction): Array[Json]= {
    logger.debug("Insert SubToComp")
    val comp_id = getCompId(comp.componentname)
    val subcomp_id = getSubId(comp.subcomponentname)
    val res = queryInsert(
      "junction_table",
      Array[String](
        "comp_id",
        "subcomp_id"
      ),
      Array[Array[String]](Array[String](
        comp_id,
        subcomp_id
      )))
    return res
  }

  /**
   * Format values as string for database insertion
   * @param value value to convert
   * @return string version on value
   */
  def getValueFormat(value: Any): String = {
    value match {
      case value: Int => value.toString()
      case value: String => f"'$value%s'"
      case value: Any => "'" + value.toString() + "'"
    }
  }

  /**
   * Insert multiple to database
   * @param data objects to insert in json
   * @return json of inserted data
   */
  def insertMany(data: Array[Json]): Json = {
    val parsed = data.map(el => 
      insertElement(
        parseIntoElement(el)
      )
    ).filter(el => el != null)
    if (parsed.length > 0)
      return valueToJson(parsed)
    return null
  }

  /**
   * insert element to table
   * @param table table name to insert to
   * @param el element to insert
   * @return map of inserted values
   */
  def insertElementRow(table: String, el: Map[String, Any]): Map[String, Any] = {
    insert(
      table, 
      el.keys.toArray, 
      Array(el.values.toArray)
    )(0)
  }

  /**
   * Parse json data to element data for insertElementRow
   * @param data json to parse
   * @return element data
   */
  def parseIntoElement(data: Json): Any = {
    data.as[module] match {
      case Left(error) =>
        println(error)
      case Right(module) =>
        return data.as[module].toOption.getOrElse(module)
    }
    data.as[component] match {
      case Left(error) =>
        println(error)
      case Right(component) =>
        return data.as[component].toOption.getOrElse(component)
    }
    data.as[singleComponent] match {
      case Left(error) =>
        println(error)
      case Right(singleComponent) =>
        return data.as[singleComponent].toOption.getOrElse(singleComponent)
    }
    data.as[subComponent] match {
      case Left(error) =>
        println(error)
      case Right(subComponent) =>
        return data.as[subComponent].toOption.getOrElse(subComponent)
    }
    return null
  }
  def insertElement(data: Any, higherOrderID: Int = -1): Any = {
    data match {
      case data: module => {
        val map = getClassMap(data)
        val comps = data.components
        val row = insertElementRow("module", map.-("components"))
        return row ++ Map("components" -> 
          comps.map(comp => insertElement(comp, row.apply("id").asInstanceOf[Int])))
      }
      case data: component => {
        val map = getClassMap(data)
        val subs = data.sub_components
        val component_row = insertElementRow(
          "component", 
          map.filter(
            { case (k, v) => 
              Array("name", "url", "version", "license", "copyright").contains(k.toString()) 
            })
        )
        val additional_data = map -- Set("sub_components", "name", "url", "version", "license", "copyright")
        val additional_row = insertElementRow(
          "module_component",
          Map("module_id" -> higherOrderID, "comp_id" -> component_row.apply("id")) ++ additional_data
        )
        return component_row ++ additional_row ++ Map("sub_components" -> 
          subs.map(sub => insertElement(sub, component_row.apply("id").asInstanceOf[Int])))
      }
      case data: singleComponent => {
        val map = getClassMap(data)
        val subs = data.sub_components
        val row = insertElementRow("component", map.-("sub_components"))
        return row ++ Map("sub_components" -> 
          subs.map(sub => insertElement(sub, row.apply("id").asInstanceOf[Int])))
      }
      case data: subComponent => {
        val map = getClassMap(data)
        val row = insertElementRow("sub_component", map)
        if (higherOrderID != -1)
          insert(
            "junction_table", 
            Array("comp_id", "subcomp_id"), 
            Array(
              Array(higherOrderID.toString(), row.apply("id").toString())
            ))
        return row
      }
      case default => null
    }
  }

  def getClassMap(cc: Product): Map[String, Any] = {
    val values = cc.productIterator
    cc.getClass.getDeclaredFields.map(_.getName -> values.next).toMap
  }
}