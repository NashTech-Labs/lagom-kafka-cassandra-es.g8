package com.knoldus.lagomkafkacassandraes.impl.kafka

import akka.actor.ActorSystem
import akka.kafka.ProducerMessage
import akka.stream.scaladsl.Flow
import akka.{Done, NotUsed}
import com.knoldus.lagomkafkacassandraes.api.{Product, ProductKafkaApi}
import com.knoldus.lagomkafkacassandraes.impl.elasticSearch.ElasticClient
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.ProductEntity
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.commands.AddProduct
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.producer.ProducerRecord
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

class ProductServiceFlow(registry: PersistentEntityRegistry)(implicit ec: ExecutionContext) {
  val productDetailsFlow: Flow[Product, Product, NotUsed] = Flow[Product].mapAsync(8) {
    product =>
      registry.refFor[ProductEntity](product.id).ask {
        AddProduct(product)
      }.map(_ => product)
  }
  implicit val actorSystem: ActorSystem = ActorSystem.create()
  val config: Config = ConfigFactory.load()
  val kafkaToKafka: Flow[Product, Done, NotUsed] = Flow[Product]
    .via(productDetailsFlow).map { product =>
    printf(s"\n\n$product\n\n")
    product
  }.map(product => Product(product.id, product.name, product.quantity))
    .via(writeToKafka)


  def writeToKafka: Flow[Product, Done, NotUsed] = writeToTheTopic("productInfo")

  def writeToTheTopic(topic: String): Flow[Product, Done, NotUsed] = {

    Flow[Product].map(product => producerMessage(topic, product.id, Json.toJson(product).toString().getBytes()))
      .map(_ => Done)
  }

  private def producerMessage(
                               topic: String,
                               key: String,
                               value: Array[Byte]
                             ): ProducerMessage.Message[String, Array[Byte], NotUsed] = {
    val record = new ProducerRecord[String, Array[Byte]](topic, key, value)
    ProducerMessage.Message(record, NotUsed)
  }

}

class TopicSubscriber(productKafkaApi: ProductKafkaApi, productServiceFlow: ProductServiceFlow) {
  productKafkaApi.productTopic.subscribe.atLeastOnce(productServiceFlow.kafkaToKafka)
  ElasticClient.kafkaToEs()
}