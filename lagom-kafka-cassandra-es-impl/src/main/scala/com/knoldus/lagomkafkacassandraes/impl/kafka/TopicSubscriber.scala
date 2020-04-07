package com.knoldus.lagomkafkacassandraes.impl.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import akka.{Done, NotUsed}
import com.knoldus.lagomkafkacassandraes.api.{Product, ProductKafkaApi}
import com.knoldus.lagomkafkacassandraes.impl.elasticSearch.ElasticClient
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.ProductEntity
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.commands.AddProduct
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

class ProductServiceFlow(registry: PersistentEntityRegistry, actorSystem: ActorSystem) {

import ProductFlow._
  val productDetailsFlow: Flow[Product, AddProduct#ReplyType, NotUsed] = Flow[Product].mapAsync(8) {
    product =>
      registry.refFor[ProductEntity](product.id).ask {
       AddProduct(product)
      }
  }


  val productTopicsDetailsFlow: Flow[Product, Done, NotUsed] = Flow[Product].via(productDetailsFlow)
    .map(_ => producerFlow("product")(actorSystem)).map(_ => Done).to(ElasticClient)

}

class TopicSubscriber(productKafkaApi: ProductKafkaApi, productServiceFlow: ProductServiceFlow) {

  productKafkaApi.publishDetailsToKafka.subscribe.atLeastOnce {
    productServiceFlow.productTopicsDetailsFlow
  }

}
