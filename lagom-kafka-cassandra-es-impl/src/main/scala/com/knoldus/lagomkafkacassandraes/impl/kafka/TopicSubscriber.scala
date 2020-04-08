package com.knoldus.lagomkafkacassandraes.impl.kafka

import akka.actor.ActorSystem
import akka.stream.scaladsl.Flow
import akka.{Done, NotUsed}
import com.knoldus.lagomkafkacassandraes.api.{Product, ProductKafkaApi, ProductKafkaOuterApi}
import com.knoldus.lagomkafkacassandraes.impl.elasticSearch.ElasticClient
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.ProductEntity
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.commands.AddProduct
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext

class ProductServiceFlow(registry: PersistentEntityRegistry, productKafkaOuterApi: ProductKafkaOuterApi)(implicit ec: ExecutionContext) {

  val productDetailsFlow: Flow[Product, Product, NotUsed] = Flow[Product].mapAsync(8) {
    product =>
      registry.refFor[ProductEntity](product.id).ask {
       AddProduct(product)
      }.map(_ => product)
  }
  val productTopicDetailsFlow = Flow[Product].via(productDetailsFlow).map(_ => productKafkaOuterApi.productDetailsTopic).map(_=> Done)
//val productToEsFlow : Flow[Product,Done,NotUsed]= Flow[Product]
//  .via(productDetailsFlow).to(ElasticClient.esSink)


}

class TopicSubscriber(productKafkaApi: ProductKafkaApi, productServiceFlow: ProductServiceFlow) {

  productKafkaApi.productTopic.subscribe.atLeastOnce {
    productServiceFlow.productTopicDetailsFlow
  }
}
