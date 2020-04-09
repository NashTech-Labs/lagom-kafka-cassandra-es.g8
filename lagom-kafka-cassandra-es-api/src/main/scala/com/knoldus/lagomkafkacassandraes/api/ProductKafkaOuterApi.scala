//package com.knoldus.lagomkafkacassandraes.api
//
//import com.lightbend.lagom.scaladsl.api.broker.Topic
//import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
//import com.lightbend.lagom.scaladsl.api.{Descriptor, Service}
//
//trait ProductKafkaOuterApi extends Service {
//  final override def descriptor: Descriptor = {
//    import Service._
//
//    named("ProductDetailsToKafka").withTopics(
//      topic("productInfo", productDetailsTopic _)
//        .addProperty(KafkaProperties.partitionKeyStrategy, PartitionKeyStrategy[Product](_.id))
//    )
//  }
//
//  def productDetailsTopic: Topic[Product]
//}
//
//
