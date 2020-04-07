package com.knoldus.lagomkafkacassandraes.impl.eventsourcing

import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.commands.{AddProduct, GetProduct}
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.events.ProductAdded
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.state.ProductState
import com.knoldus.lagomkafkacassandraes.api.Product
import com.lightbend.lagom.scaladsl.playjson.{JsonSerializer, JsonSerializerRegistry}

import scala.collection.immutable

object ProductSerializerRegistry extends JsonSerializerRegistry {
  override def serializers: immutable.Seq[JsonSerializer[_]] = immutable.Seq(
    JsonSerializer[Product],
    JsonSerializer[AddProduct],
    JsonSerializer[GetProduct],
    JsonSerializer[ProductAdded],
    JsonSerializer[ProductState]
  )
}
