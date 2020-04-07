package com.knoldus.lagomkafkacassandraes.impl.eventsourcing.state

import play.api.libs.json.{Format, Json}
import com.knoldus.lagomkafkacassandraes.api.Product

case class ProductState(product:Option[Product])

object ProductState {
  implicit val format: Format[ProductState] = Json.format
}
