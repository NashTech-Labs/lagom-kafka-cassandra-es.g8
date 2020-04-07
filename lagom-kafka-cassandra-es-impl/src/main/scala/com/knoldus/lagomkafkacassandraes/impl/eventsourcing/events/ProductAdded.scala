package com.knoldus.lagomkafkacassandraes.impl.eventsourcing.events

import com.knoldus.Events
import com.knoldus.lagomkafkacassandraes.api.Product
import play.api.libs.json.{Format, Json}

case class ProductAdded (product: Product) extends Events

object ProductAdded {
  implicit val format: Format[ProductAdded] = Json.format
}

