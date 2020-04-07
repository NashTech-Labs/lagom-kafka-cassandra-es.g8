package com.knoldus.lagomkafkacassandraes.impl.eventsourcing.commands

import akka.Done
import com.knoldus.ProductCommands
import play.api.libs.json.{Format, Json}
import com.knoldus.lagomkafkacassandraes.api.Product

/**
  * add product command
  *
  * @param product -the product object
  */
case class AddProduct(product: Product) extends ProductCommands[Done]

object AddProduct {
  implicit val format: Format[AddProduct] = Json.format
}
