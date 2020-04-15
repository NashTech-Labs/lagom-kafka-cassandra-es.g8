package com.knoldus.lagomkafkacassandraes.impl.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.stream.scaladsl.Flow
import akka.{Done, NotUsed}
import com.knoldus.lagomkafkacassandraes.api.{Product, ProductKafkaApi}
import com.knoldus.lagomkafkacassandraes.impl.elasticSearch.ElasticClient
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.ProductEntity
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.commands.AddProduct
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

class ProductServiceFlow(registry: PersistentEntityRegistry)(implicit ec: ExecutionContext) {

 implicit val actorSystem: ActorSystem = ActorSystem.create()
  val productDetailsFlow: Flow[Product, Product, NotUsed] = Flow[Product].mapAsync(8) {
    product =>
      registry.refFor[ProductEntity](product.id).ask {
       AddProduct(product)
      }.map(_ =>product)
  }

  val config: Config = ConfigFactory.load()
private def producerMessage(
                             topic: String,
                             key: String,
                             value: Array[Byte]
                           ): ProducerMessage.Message[String, Array[Byte], NotUsed] = {
  val record = new ProducerRecord[String, Array[Byte]](topic,key,value)
  ProducerMessage.Message(record, NotUsed)
}

  def producerSettings(actorSystem: ActorSystem
                                        ): ProducerSettings[String, Array[Byte]] = {
    val (keySerializer, valueSerializer) = (new StringSerializer, new ByteArraySerializer)
    val baseSettings = ProducerSettings(actorSystem, keySerializer, valueSerializer)
    baseSettings.withBootstrapServers(config.getString("akka.kafka.brokers"))
  }
  def writeToTheTopic(topic:String):Flow[Product,Done,NotUsed] = {
    val prodSettings = producerSettings(actorSystem)
   val kafkaProducer =prodSettings.createKafkaProducer()
    Flow[Product].map(product =>producerMessage(topic,product.id, Json.toJson(product).toString().getBytes()))
      .via(Producer.flexiFlow(prodSettings)).map(_ => Done)
  }
def writeToKafka: Flow[Product, Done, NotUsed] =writeToTheTopic("productInfo")

  val kafkaToKafka: Flow[Product, Done, NotUsed] = Flow[Product]
  .via(productDetailsFlow).map { product =>
    println(s"\n\n$product\n\n")
    product
  }.map(product => Product(product.id, product.name, product.quantity))
  .via(writeToKafka)

}

class TopicSubscriber(productKafkaApi: ProductKafkaApi, productServiceFlow: ProductServiceFlow) {
  productKafkaApi.productTopic.subscribe.atLeastOnce( productServiceFlow.kafkaToKafka)
  ElasticClient.kafkaToEs()
}