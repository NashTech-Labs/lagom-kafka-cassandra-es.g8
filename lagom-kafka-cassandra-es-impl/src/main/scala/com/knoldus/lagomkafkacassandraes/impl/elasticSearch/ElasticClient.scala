package com.knoldus.lagomkafkacassandraes.impl.elasticSearch
import akka.NotUsed
import akka.stream.alpakka.elasticsearch.scaladsl.ElasticsearchFlow
import akka.stream.alpakka.elasticsearch.{WriteMessage, WriteResult}
import akka.stream.scaladsl.Flow
import com.knoldus.lagomkafkacassandraes.api.Product
import org.apache.http.HttpHost
import org.apache.kafka.clients.consumer.ConsumerRecord
import org.elasticsearch.client.RestClient
import play.api.libs.json.Json
import spray.json.{JsNumber, JsObject, JsString, JsonWriter}

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
////val flow: Flow[WriteMessage[Nothing, NotUsed], WriteResult[Nothing, NotUsed], NotUsed] =ElasticsearchFlow.create("productIndex","docs",ElasticsearchWriteSettings.Default,client,objectMapper)
  val interFlow: Flow[ConsumerRecord[Array[Byte], String], WriteMessage[Product, NotUsed], NotUsed] = Flow[ConsumerRecord[Array[Byte], String]].map { message =>

    // Parsing the record as Company Object
    val product = Json.parse(message.value()).as[Product]
    val id = product.id


    // Transform message so that we can write to elastic

    WriteMessage.createIndexMessage(id,product)
  }
//  //val sink = Sink.fromGraph(GraphDSL.create(esSink))
// val flow: Flow[Product, Done.type, NotUsed] = Flow[Product]
//   .map(product => WriteMessage.createIndexMessage(product.id,product)).map(_ => Done)
////val flow: Flow[Any, Nothing, Nothing] = Flow.fromGraph(GraphDSL.create(interFlow))
//  val esSink: Sink[WriteMessage[Product, NotUsed], Future[Done]] = ElasticsearchSink
//    .create[Product]("productIndex","products")
val esSink: Flow[WriteMessage[Product, NotUsed], WriteResult[Product, NotUsed], NotUsed] =
ElasticsearchFlow.create[Product]("productIndex","products")(client,jsonWriter)


}
