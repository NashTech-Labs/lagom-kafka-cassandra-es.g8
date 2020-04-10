package com.knoldus.constants

import com.typesafe.config.{Config, ConfigFactory}

/**
  * Constants that are used in template
  */
object Constants {
  val config: Config = ConfigFactory.load()
  val KEYSPACE_NAME: String = config.getString("user.cassandra.keyspace")
  val TABLE_NAME: String = config.getString("cassandra.tableName")
  val INPUT_TOPIC:String = config.getString("product.input.topic")
  val OUTPUT_TOPIC:String = config.getString("product.output.topic")
}
