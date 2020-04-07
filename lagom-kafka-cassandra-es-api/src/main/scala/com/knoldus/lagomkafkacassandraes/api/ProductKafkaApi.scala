package com.knoldus.lagomkafkacassandraes.api

import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service}


trait ProductKafkaApi extends Service {

  def publishDetailsToKafka: Topic[Product]

  final override def descriptor: Descriptor = {
    import Service._

    named("CustomerDetailsKafka").withTopics(
      topic("product", publishDetailsToKafka _)
        .addProperty(KafkaProperties.partitionKeyStrategy, PartitionKeyStrategy[Product](_.id))
    )
  }
}
