package com.knoldus.lagomkafkacassandraes.impl.eventsourcing

import com.knoldus.lagomkafkacassandraes.api.Product
import akka.Done
import com.datastax.driver.core.{BoundStatement, PreparedStatement}
import com.knoldus.Events
import com.knoldus.constants.Queries
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.events.ProductAdded
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, ReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.cassandra.{CassandraReadSide, CassandraSession}

import scala.concurrent.{ExecutionContext, Future}

class ProductReadSideProcessor(cassandraSession: CassandraSession, readSide: CassandraReadSide)
                              (implicit ec: ExecutionContext) extends ReadSideProcessor[Events] {
  var addEntity: PreparedStatement = _

  override def aggregateTags: Set[AggregateEventTag[Events]] =
    Events.Tag.allTags

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[Events] =
    readSide.builder[Events]("productOffset")
      .setGlobalPrepare(() => createTable())
      .setPrepare(_ => prepareStatements())
      .setEventHandler[ProductAdded](ese => addEntity(ese.event.product))
      .build()

  def createTable(): Future[Done] = {
    cassandraSession.executeCreateTable(Queries.CREATE_TABLE
      .stripMargin)
  }

  def prepareStatements(): Future[Done] =
    for {
      productPreparedStatement <- cassandraSession.prepare(Queries.INSERT_PRODUCT)
    } yield {
      addEntity = productPreparedStatement
      Done
    }

  def addEntity(product: Product): Future[List[BoundStatement]] = {
    val bindInsertProduct: BoundStatement = addEntity.bind()
    bindInsertProduct.setString("id", product.id)
    bindInsertProduct.setString("name", product.name)
    bindInsertProduct.setLong("quantity", product.quantity)
    Future.successful(List(bindInsertProduct))
  }

}