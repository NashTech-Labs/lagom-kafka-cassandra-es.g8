package com.knoldus.lagomkafkacassandraes.impl

import com.knoldus.lagomkafkacassandraes.api.{ProductApi, ProductKafkaApi, ProductKafkaOuterApi}
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.{ProductEntity, ProductReadSideProcessor, ProductSerializerRegistry}
import com.knoldus.lagomkafkacassandraes.impl.kafka.{ProductServiceFlow, TopicSubscriber}
import com.lightbend.lagom.scaladsl.api.ServiceLocator.NoServiceLocator
import com.lightbend.lagom.scaladsl.api.{Descriptor, ServiceLocator}
import com.lightbend.lagom.scaladsl.broker.kafka.LagomKafkaComponents
import com.lightbend.lagom.scaladsl.devmode.LagomDevModeComponents
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.playjson.JsonSerializerRegistry
import com.lightbend.lagom.scaladsl.server._
import com.softwaremill.macwire._
import play.api.libs.ws.ahc.AhcWSComponents

class ProductLoader extends LagomApplicationLoader {

  override def load(context: LagomApplicationContext): LagomApplication =
    new ProductApplication(context) {
      override def serviceLocator: ServiceLocator = NoServiceLocator
    }

  override def loadDevMode(context: LagomApplicationContext): LagomApplication =
    new ProductApplication(context) with LagomDevModeComponents

  override def describeService: Option[Descriptor] = Some(readDescriptor[ProductApi])
}

abstract class ProductApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with CassandraPersistenceComponents
    with LagomKafkaComponents
    with AhcWSComponents {

  // Bind the service this server provides
  override lazy val lagomServer: LagomServer = serverFor[ProductApi](wire[ProductImpl])
  lazy val productServiceFlow: ProductServiceFlow = wire[ProductServiceFlow]

  lazy val productKafkaApi: ProductKafkaApi = serviceClient.implement[ProductKafkaApi]
  lazy val productKafkaOuterApi:ProductKafkaOuterApi = serviceClient.implement[ProductKafkaOuterApi]

  cluster.registerOnMemberUp(wire[TopicSubscriber])
  // Register the JSON serializer registry
  override lazy val jsonSerializerRegistry: JsonSerializerRegistry = ProductSerializerRegistry
  //Register the persistent entity
  persistentEntityRegistry.register(wire[ProductEntity])
  //Register read side processor
  readSide.register(wire[ProductReadSideProcessor])

}