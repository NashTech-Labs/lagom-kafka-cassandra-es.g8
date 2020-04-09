package com.knoldus.lagomkafkacassandraes.impl.elasticSearch
import akka.actor.ActorSystem
import akka.kafka.{ConsumerSettings, Subscriptions}
import akka.kafka.scaladsl.Consumer
import akka.stream.ActorMaterializer
import akka.{Done, NotUsed}
import akka.stream.alpakka.elasticsearch.scaladsl.{ElasticsearchFlow, ElasticsearchSink}
import akka.stream.alpakka.elasticsearch.{WriteMessage, WriteResult}
import akka.stream.scaladsl.{Flow, Sink, Source}
import com.knoldus.lagomkafkacassandraes.api.Product
import org.apache.http.HttpHost
import org.apache.kafka.clients.consumer.{ConsumerConfig, ConsumerRecord}
import org.apache.kafka.common.serialization.{ByteArrayDeserializer, StringDeserializer}
import org.elasticsearch.client.RestClient
import play.api.libs.json.Json
import spray.json.DefaultJsonProtocol.jsonFormat2
import spray.json.{JsNumber, JsObject, JsString, JsonFormat, JsonWriter}

import scala.concurrent.Future

object ElasticClient {

  private val port = 9200
  private val host = "localhost"
  private val scheme = "http"

  val client:RestClient= RestClient.builder(new HttpHost(host, port, scheme)).build()
 val jsonWriter:JsonWriter[Product] = (product: Product) => {
   JsObject(
     "id" -> JsString(product.id),
     "name" -> JsString(product.name),
  "quantity"-> JsNumber(product.quantity))
 }
//////val flow: Flow[WriteMessage[Nothing, NotUsed], WriteResult[Nothing, NotUsed], NotUsed] =ElasticsearchFlow.create("productIndex","docs",ElasticsearchWriteSettings.Default,client,objectMapper)
//  val interFlow: Flow[ConsumerRecord[Array[Byte], String], WriteMessage[Product, NotUsed], NotUsed] = Flow[ConsumerRecord[Array[Byte], String]].map { message =>
//
//    // Parsing the record as Company Object
//    val product = Json.parse(message.value()).as[Product]
//    val id = product.id
//
//
//    // Transform message so that we can write to elastic
//
//    WriteMessage.createIndexMessage(id,product)
//  }
////  //val sink = Sink.fromGraph(GraphDSL.create(esSink))
//// val flow: Flow[Product, Done.type, NotUsed] = Flow[Product]
////   .map(product => WriteMessage.createIndexMessage(product.id,product)).map(_ => Done)
//////val flow: Flow[Any, Nothing, Nothing] = Flow.fromGraph(GraphDSL.create(interFlow))
//  val esSink: Sink[WriteMessage[Product, NotUsed], Future[Done]] = ElasticsearchSink
//    .create[Product]("productIndex","products")(client,jsonWriter)
val esSink: Flow[WriteMessage[Product, NotUsed], WriteResult[Product, NotUsed], NotUsed] =
ElasticsearchFlow.create[Product]("productIndex","products")(client,jsonWriter)

//  implicit val system: ActorSystem = ActorSystem.create()
//
//  implicit val mat: ActorMaterializer = ActorMaterializer()
//  implicit val _ = Json.format[Product]
//
//  implicit val client: RestClient = RestClient.builder(new HttpHost("localhost", 9200)).build()
//
//
//  implicit val format: JsonFormat[Company] = jsonFormat2(Company)
//
//
//  val intermediateFlow = Flow[ConsumerRecord[Array[Byte], String]].map { kafkaMessage =>
//
//    // Parsing the record as Company Object
//    val company = Json.parse(kafkaMessage.value()).as[Company]
//    val loc = company.location
//
//
//    // Transform message so that we can write to elastic
//
//    WriteMessage.createIndexMessage(loc, company)
//  }
//
//
//  val consumerSettings = ConsumerSettings(system, new ByteArrayDeserializer, new StringDeserializer)
//    .withBootstrapServers("localhost:9092")
//    .withGroupId("akka-stream-kafka-test")
//    .withProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest")
//
//  val kafkaSource: Source[ConsumerRecord[Array[Byte], String], Consumer.Control] =
//    Consumer.plainSource(consumerSettings, Subscriptions.topics("topic221"))
//
//
//  val esSink = ElasticsearchSink.create[Company](
//    indexName = "sink1",
//    typeName = "company"
//  )
//
//
//  kafkaSource
//    .via(intermediateFlow)
//    .runWith(esSink)
}
