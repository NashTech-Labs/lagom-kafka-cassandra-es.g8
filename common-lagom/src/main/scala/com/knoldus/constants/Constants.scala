package com.knoldus.constants

import com.typesafe.config.{Config, ConfigFactory}

object Constants {
  val config: Config = ConfigFactory.load()
  val KEYSPACE_NAME: String = config.getString("user.cassandra.keyspace")
  val TABLE_NAME: String = config.getString("cassandra.tableName")
}
