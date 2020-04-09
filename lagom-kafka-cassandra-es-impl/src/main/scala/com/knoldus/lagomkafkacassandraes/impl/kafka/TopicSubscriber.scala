package com.knoldus.lagomkafkacassandraes.impl.kafka

import akka.{Done, NotUsed}
import akka.stream.alpakka.elasticsearch.WriteMessage
import akka.stream.scaladsl.Flow
import com.knoldus.lagomkafkacassandraes.api.{Product, ProductKafkaApi}
import com.knoldus.lagomkafkacassandraes.impl.elasticSearch.ElasticClient
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry

import scala.concurrent.ExecutionContext

class ProductServiceFlow(registry: PersistentEntityRegistry)(implicit ec: ExecutionContext) {

//  val productDetailsFlow: Flow[Product, Product, NotUsed] = Flow[Product].mapAsync(8) {
//    product =>
//      registry.refFor[ProductEntity](product.id).ask {
//       AddProduct(product)
//      }.map(_ => product)
//  }
  val productTopicDetailsFlow: Flow[Product, Done.type, NotUsed] = Flow[Product].map { product =>
    println(s"\n\n$product\n\n")
  product
  }.map(product => Product(product.id, product.name, product.quantity))
    .map(product => WriteMessage.createIndexMessage(product.id, product)).via(ElasticClient.esSink).map { messageResults =>
    if (!messageResults.success) {
      //      log.error(s"Error writing $docType objects to $esIndex: ${result.error.getOrElse("")}")
      throw new Exception(s"Failed to write to ES")
    }
    Done
  }
//  val productTopicDetailsFlow: Flow[Product, Done.type, NotUsed] = Flow[Product]
//    .via(productDetailsFlow).map(_ => productKafkaOuterApi.productDetailsTopic).map(_=> Done)
////val productToEsFlow : Sink[Product, NotUsed]= Flow[Product]
////  .via(productDetailsFlow)
////  .via(ElasticClient.flow)
////  .to(sink = ElasticClient.esSink )
}

class TopicSubscriber(productKafkaApi: ProductKafkaApi, productServiceFlow: ProductServiceFlow) {

  productKafkaApi.productTopic.subscribe.atLeastOnce {
    productServiceFlow.productTopicDetailsFlow
  }
}
