package com.knoldus.lagomkafkacassandraes.api

import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.api.broker.kafka.{KafkaProperties, PartitionKeyStrategy}
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service}


trait ProductKafkaApi extends Service {

  def productTopic: Topic[Product]

  final override def descriptor: Descriptor = {
    import Service._

    named("ProductDetailsKafka").withTopics(
      topic("prod", productTopic _)
        .addProperty(KafkaProperties.partitionKeyStrategy, PartitionKeyStrategy[Product](_.id))
    )
  }
}
