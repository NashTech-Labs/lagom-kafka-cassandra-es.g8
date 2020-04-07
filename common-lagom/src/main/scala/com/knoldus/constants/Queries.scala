package com.knoldus.constants

import com.typesafe.config.{Config, ConfigFactory}

object Queries {

  val config: Config = ConfigFactory.load()

  val CREATE_TABLE =
    s"""CREATE TABLE IF NOT EXISTS ${Constants.KEYSPACE_NAME}.${Constants.TABLE_NAME} (
       |id text PRIMARY KEY, name text, quantity bigint)"""

  // Get the details of a product from a database by product id.
  val GET_PRODUCT = s"SELECT * FROM ${Constants.TABLE_NAME} WHERE id =?"

  //Insert a product details into database
  val INSERT_PRODUCT = s"INSERT INTO ${Constants.KEYSPACE_NAME}.${Constants.TABLE_NAME} (id, name, quantity) VALUES (?, ?, ?)"
}
