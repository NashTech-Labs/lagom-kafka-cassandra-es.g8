//package com.knoldus.lagomkafkacassandraes.impl.kafka
//
//import com.knoldus.Events
//import com.knoldus.lagomkafkacassandraes.api.{Product, ProductKafkaOuterApi}
//import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.events.ProductAdded
//import com.lightbend.lagom.scaladsl.api.broker.Topic
//import com.lightbend.lagom.scaladsl.broker.TopicProducer
//import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
//import org.slf4j.{Logger, LoggerFactory}
//
//class ProductKafkaOuterApiImpl(persistentEntityRegistry: PersistentEntityRegistry) extends ProductKafkaOuterApi{
//
//  private val log: Logger = LoggerFactory.getLogger(getClass)
//
//  override def productDetailsTopic: Topic[Product] = TopicProducer.taggedStreamWithOffset(Events.Tag.allTags.toList) { (tag, offset) =>
//    persistentEntityRegistry.eventStream(tag, offset)
//      .collect {
//        case EventStreamElement(_,ProductAdded(product), pOffset) =>
//          log.debug(s"Writing product: ${product.name} to kafka")
//          Product(product.id,product.name,product.quantity) -> pOffset
//      }
//  }
//
//}
