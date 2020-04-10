package com.knoldus.lagomkafkacassandraes.impl.kafka

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Producer
import akka.kafka.{ProducerMessage, ProducerSettings}
import akka.stream.scaladsl.Flow
import akka.{Done, NotUsed}
import com.knoldus.constants.Constants
import com.knoldus.lagomkafkacassandraes.api.{Product, ProductKafkaApi}
import com.knoldus.lagomkafkacassandraes.impl.elasticSearch.ElasticClient
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.ProductEntity
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.commands.AddProduct
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.Json

import scala.concurrent.ExecutionContext

/**
  * It is used to create flow
  * @param registry an instance of PersistentEntityRegistry
  * @param ec an instance of executioncontext
  */
class ProductServiceFlow(registry: PersistentEntityRegistry)(implicit ec: ExecutionContext) {
val log: Logger = LoggerFactory.getLogger(getClass)
  implicit val actorSystem: ActorSystem = ActorSystem.create()
  val config: Config = ConfigFactory.load()
  /**
    * produces a flow that takes an object of Product and returns the same
    */
  val productDetailsFlow: Flow[Product, Product, NotUsed] = Flow[Product].mapAsync(8) {
    product =>
      registry.refFor[ProductEntity](product.id).ask {
        AddProduct(product)
      }.map(_ => product)
  }
  /**
    * a flow to transfer data from one topic of kafka to another
    */
  val kafkaToKafka: Flow[Product, Done, NotUsed] = Flow[Product]
    .via(productDetailsFlow).map { product =>
    log.debug(s"\n\n$product\n\n")
    product
  }.map(product => Product(product.id, product.name, product.quantity))
    .via(writeToKafka)

 private def writeToKafka: Flow[Product, Done, NotUsed] = writeToTheTopic(Constants.OUTPUT_TOPIC)

  def writeToTheTopic(topic: String): Flow[Product, Done, NotUsed] = {
    val prodSettings = producerSettings(actorSystem)
    val kafkaProducer = prodSettings.createKafkaProducer()
    Flow[Product].map(product => producerMessage(topic, product.id, Json.toJson(product).toString().getBytes()))
      .via(Producer.flexiFlow(prodSettings)).map(_ => Done)
  }

  private def producerMessage(
                               topic: String,
                               key: String,
                               value: Array[Byte]
                             ): ProducerMessage.Message[String, Array[Byte], NotUsed] = {
    val record = new ProducerRecord[String, Array[Byte]](topic, key, value)
    ProducerMessage.Message(record, NotUsed)
  }

  private def producerSettings(actorSystem: ActorSystem
                      ): ProducerSettings[String, Array[Byte]] = {
    val (keySerializer, valueSerializer) = (new StringSerializer, new ByteArraySerializer)
    val baseSettings = ProducerSettings(actorSystem, keySerializer, valueSerializer)
    baseSettings.withBootstrapServers(config.getString("akka.kafka.brokers"))
  }

}

/**
  * a class that subscribes to the topic
  * @param productKafkaApi an instance of ProductKafkaApi
  * @param productServiceFlow an instance of ProductServiceFlow
  */
class TopicSubscriber(productKafkaApi: ProductKafkaApi, productServiceFlow: ProductServiceFlow) {
  productKafkaApi.productTopic.subscribe.atLeastOnce(productServiceFlow.kafkaToKafka)
  ElasticClient.kafkaToEs()
}