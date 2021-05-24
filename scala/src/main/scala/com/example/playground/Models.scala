package com.example.playground

/**
 * Class to store database model objects, used when inserting single objects to database
 */
object dbmodels {
  case class module (
    name: String
  );

  case class junction (
    componentname: String,
    subcomponentname: String
                      );

  case class componentToModule (
   modulename: String,
   componentname: String,
   usage_type: String,
   attr_value1: String,
   attr_value2: String,
   attr_value3: String,
   date: String,
   comment_one: String,
   comment_two: String
  );

  case class component (
    name: String,
    url: String,
    version: String,
    license: String,
    copyright: String
  );

  case class subComponent (
    name: String,
    url: String,
    version: String,
    license: String,
    copyright: String
  );

  case class moduleComponent (
    usage_type: String,
    attr_value1: String,
    attr_value2: String,
    attr_value3: String,
    date: String,
    comment_one: String,
    comment_two: String
  );

  case class subComponentToComponent (
    componentname: String,
    name: String,
    url: String,
    version: String,
    license: String,
    copyright: String
  );
}

/**
 * Database modles used when inserting in bulk
 */
object bulkModels {
  case class module (
    name: String,
    components: Array[component]
  );

  case class component (
    name: String,
    url: String,
    version: String,
    license: String,
    copyright: String,
    usage_type: String,
    attr_value1: String,
    attr_value2: String,
    attr_value3: String,
    date: String,
    comment_one: String,
    comment_two: String,
    sub_components: Array[subComponent]
  );

  case class singleComponent (
    name: String,
    url: String,
    version: String,
    license: String,
    copyright: String,
    sub_components: Array[subComponent]
  )

  case class subComponent (
    name: String,
    url: String,
    version: String,
    license: String,
    copyright: String
  )
}