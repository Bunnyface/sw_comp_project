package com.example.playground

import scala.collection.immutable.Stream
import java.sql.{ResultSet}


object retrieveFunctions{
  case class Module(
    info: Array[String],
    components: Array[Array[String]]
  );

  def get(
    table: String,
    columns: String = "*",
    cond: String = null
  ): List[IndexedSeq[Object]] = {
    val query = if (cond == null) {
      f"SELECT $columns%s FROM $table%s"
    } else {
      f"SELECT $columns%s FROM $table%s WHERE $cond%s"
    }

    val sqlClient = new Client();
    sqlClient.connect("defaultdb");

    val result = sqlClient.fetch(query);
    val metaData = result.getMetaData();
    val colLength = metaData.getColumnCount();

    val tableValues = new Iterator[IndexedSeq[Object]] {
      def hasNext = result.next()
      def next() = {
        for (i <- 1 to colLength)
          yield result.getObject(i)
      }
    }.toStream

    return tableValues.toList;
  }

  def queryNames(): List[String] = {
    val resList = get("module", "name");
    return resList.map(row => row(0).toString());
  }

  def queryRelease(moduleName: String): Module = {
    val fetched = try {
      get("module", "*", f"name='$moduleName%s'")(0);
    } catch {
      case _: Throwable => null;
    }

    if (fetched == null)
      return null;

    val moduleID = fetched(0).asInstanceOf[Int];
    val componentData = get(
      "module_component as mc, component as c",
      "c.name, c.version",
      f"mc.module_id=$moduleID%d AND mc.comp_id=c.id"
    ).map(row => row.map(v => v.toString()).toArray);

    return Module(
      Array(fetched(1).toString()),
      componentData.toArray
    );
  }
}
