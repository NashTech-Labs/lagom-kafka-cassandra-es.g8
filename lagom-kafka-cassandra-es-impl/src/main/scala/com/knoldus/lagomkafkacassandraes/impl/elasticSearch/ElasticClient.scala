package com.knoldus.lagomkafkacassandraes.impl.elasticSearch

import akka.actor.ActorSystem
import akka.kafka.scaladsl.Consumer
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.stream.alpakka.elasticsearch.WriteMessage
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchSink
import akka.stream.scaladsl.{Flow, Sink, Source}
import akka.{Done, NotUsed}
import com.knoldus.lagomkafkacassandraes.api.Product
import org.apache.http.HttpHost
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.elasticsearch.client.RestClient
import play.api.libs.json.Json
import spray.json.{JsNumber, JsObject, JsString, JsonWriter}

import scala.concurrent.Future

object ElasticClient {
  implicit val system: ActorSystem = ActorSystem.create()

  private val port = 9200
  private val host = "localhost"
  private val scheme = "http"
  val client: RestClient = RestClient.builder(new HttpHost(host, port, scheme)).build()
  val jsonWriter: JsonWriter[Product] = (product: Product) => {
    JsObject(
      "id" -> JsString(product.id),
      "name" -> JsString(product.name),
      "quantity" -> JsNumber(product.quantity))
  }
  val intermediateFlow: Flow[ConsumerRecord[Array[Byte], String], WriteMessage[Product, NotUsed], NotUsed] =
    Flow[ConsumerRecord[Array[Byte], String]].map { message =>

      // Parsing the record as Product Object
      val product = Json.parse(message.value()).as[Product]
      val id = product.id


      // Transform message so that we can write to elastic

      WriteMessage.createIndexMessage(id, product)
    }
  val esSink: Sink[WriteMessage[Product, NotUsed], Future[Done]] = ElasticsearchSink
    .create[Product]("product_index", "products")(client, jsonWriter)
  val consumerSettings: ConsumerSettings[Array[Byte], String] =
    ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
      .withBootstrapServers("localhost:9092")
      .withGroupId("akka-stream-kafka")
      .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
  val kafkaSource: Source[ConsumerRecord[Array[Byte], String], Consumer.Control] =
    Consumer.plainSource(consumerSettings, Subscriptions.topics("prod"))


  def kafkaToEs(): Future[Done] =
    kafkaSource
      .via(intermediateFlow)
      .runWith(esSink)
}
