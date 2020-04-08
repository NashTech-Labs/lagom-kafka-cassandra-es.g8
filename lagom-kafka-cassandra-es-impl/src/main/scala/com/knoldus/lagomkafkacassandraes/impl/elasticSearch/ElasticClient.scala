package com.knoldus.lagomkafkacassandraes.impl.elasticSearch
import akka.stream.alpakka.elasticsearch.{ElasticsearchWriteSettings, WriteMessage}
import akka.stream.alpakka.elasticsearch.javadsl.ElasticsearchFlow
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchSink
import akka.stream.scaladsl.{Flow, Sink}
import akka.{Done, NotUsed}
import com.fasterxml.jackson.databind.ObjectMapper
import com.knoldus.lagomkafkacassandraes.api.Product
import org.apache.http.HttpHost
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.elasticsearch.client.RestClient
import play.api.libs.json.Json
import spray.json.{JsObject, JsString, JsonWriter}

import scala.concurrent.Future

object ElasticClient {

  private val port = 9200
  private val host = "localhost"
  private val scheme = "http"

 implicit val client:RestClient= RestClient.builder(new HttpHost(host, port, scheme)).build()
  val objectMapper = new ObjectMapper()
 implicit val jsonWriter:JsonWriter[Product] = (product: Product) => {
   JsObject(
     "id" -> JsString(product.id),
     "name" -> JsString(product.name))
 }
//val flow =ElasticsearchFlow.create("productIndex","docs",ElasticsearchWriteSettings.Default,client,objectMapper)
  val flow = Flow[ConsumerRecord[Array[Byte], String]].map { message =>

    // Parsing the record as Company Object
    val product = Json.parse(message.value()).as[Product]
    val id = product.id


    // Transform message so that we can write to elastic

    WriteMessage.createIndexMessage(id,product)
  }


  val esSink: Sink[WriteMessage[Product, NotUsed], Future[Done]] = ElasticsearchSink.create[Product]("productIndex","products")
}
