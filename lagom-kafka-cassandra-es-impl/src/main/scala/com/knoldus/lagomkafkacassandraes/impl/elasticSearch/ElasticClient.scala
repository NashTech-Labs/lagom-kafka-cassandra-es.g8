package com.knoldus.lagomkafkacassandraes.impl.elasticSearch
import java.util.concurrent.CompletionStage

import akka.{Done, NotUsed}
import akka.stream.alpakka.elasticsearch.WriteMessage
import akka.stream.alpakka.elasticsearch.javadsl.ElasticsearchSink
import akka.stream.javadsl.Sink
import org.apache.http.HttpHost
import org.elasticsearch.client.RestClient

object ElasticClient {

  private val port = 9200
  private val host = "localhost"
  private val scheme = "http"

  implicit val client:RestClient= RestClient.builder(new HttpHost(host, port, scheme)).build()


 // val esSink: Sink[WriteMessage[Nothing, NotUsed], CompletionStage[Done]] = ElasticsearchSink.create("sink","doc")
}
