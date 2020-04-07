package com.knoldus.lagomkafkacassandraes.impl.eventsourcing.commands

import com.knoldus.ProductCommands
import play.api.libs.json.{Format, Json}
import com.knoldus.lagomkafkacassandraes.api.Product

case class GetProduct(id: String) extends ProductCommands[Product]

object GetProduct {
  implicit val format: Format[GetProduct] = Json.format
}

