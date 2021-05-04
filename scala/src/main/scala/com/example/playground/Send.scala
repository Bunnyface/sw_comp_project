package com.example.playground

import io.circe._
import io.circe.syntax._
import retrieveFunctions._

object sendFunctions {
  def queryInsert(
                   table: String,
                   columns: Array[String],
                   data: Array[Array[String]]
                 ): Array[Json] = {
    println("INSERTING")
    if (data == null || data.length == 0)
      return Array();

    val sqlClient = new Client();
    sqlClient.connect("defaultdb");

    val columnString = columns.mkString(", ");

    val result: Array[Json] = data.map(array => {
      val row = "'" + array.mkString("', '") + "'";
      val query = f"INSERT INTO $table%s ($columnString%s) VALUES ($row%s) RETURNING *;";

      try {
        mapToJson(
          resultSetToMapArray(
            sqlClient.execute(query)
          )(0)
        );
      } catch {
        case _: Throwable =>
          println("Insert wasn't successful");
          null;
      }
    }).filter(row => row != null);

    sqlClient.close();
    return result;
  }

  def queryUpdate(
                   table: String,
                   newValCol: String,
                   newVal: String,
                   condCol: String,
                   condVal: String
                 ): Json = {
    val sqlClient = new Client();
    sqlClient.connect("defaultdb");
    println("Sending update query");

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
        println("Update wasn't successful")
    }
    sqlClient.close();
    println("RETURNING NULL");
    return null;
  }

  def insertModule(mod: dbmodels.module): Array[Json]= {
    print("Insert Module")
    val res = queryInsert(
      "module",
      Array[String]{"name"},
      Array[Array[String]]{Array[String]{mod.name}}
    )
    return res
  }

  def insertComponent(comp: dbmodels.component): Array[Json]= {
    print("Insert Component")
    val res = queryInsert(
      "component",
      Array[String]("name", "url", "version", "license", "copyright"),
      Array[Array[String]](Array[String](comp.name, comp.url, comp.version, comp.license, comp.copyright))
    )
    return res
  }
  def insertSubComponent(comp: dbmodels.subComponent): Array[Json]= {
    print("Insert subComponent")
    val res = queryInsert(
      "sub_component",
      Array[String]("name", "url", "version", "license", "copyright"),
      Array[Array[String]](Array[String](comp.name, comp.url, comp.version, comp.license, comp.copyright))
    )
    return res
  }

  def insertComponentToModel(comp: dbmodels.componentToModule): Array[Json]= {
    print("Insert ComponentToModel")
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
  def insertSubToComp(comp: dbmodels.junction): Array[Json]= {
    print("Insert SubToComp")
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
}