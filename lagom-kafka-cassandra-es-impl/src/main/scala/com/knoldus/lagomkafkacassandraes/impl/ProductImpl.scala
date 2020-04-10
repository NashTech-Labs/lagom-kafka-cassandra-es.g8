package com.knoldus.lagomkafkacassandraes.impl

import akka.util.Timeout
import akka.{Done, NotUsed}
import com.knoldus.{Events, ProductCommands}
import com.knoldus.constants.Queries
import com.knoldus.lagomkafkacassandraes.api.{Product, ProductApi}
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.ProductEntity
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.commands.AddProduct
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.events.ProductAdded
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.cassandra.CassandraSession
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRef, PersistentEntityRegistry}

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}
import org.slf4j.{ Logger, LoggerFactory }
/**
  * Implementation of the ProductApi
  */
class ProductImpl(
                   persistentEntityRegistry: PersistentEntityRegistry, session: CassandraSession
                 )(implicit ec: ExecutionContext)
  extends ProductApi {

  implicit val timeout: Timeout = Timeout(5.seconds)

  override def getProductDetails(id: String): ServiceCall[NotUsed, String] = ServiceCall { _ =>
    getProductById(id).map(product => s"Product named ${product.get.name} has id: $id and quantity in inventory: ${product.get.quantity}")
  }

  def getProductById(id: String): Future[Option[Product]] =
    session.selectOne(Queries.GET_PRODUCT,id).map { rows =>
      rows.map { row =>
        val id = row.getString("id")
        val name = row.getString("name")
        val quantity = row.getLong("quantity")
        Product(id, name, quantity)
      }
    }

  override def addProduct(): ServiceCall[Product, String] = ServiceCall { product =>
    ref(product.id).ask(AddProduct(product)).map {
      case Done =>
        s"${product.name} is added"
    }
  }

  private val log: Logger = LoggerFactory.getLogger(getClass)

  override def productDetailsTopic: Topic[Product] = TopicProducer.taggedStreamWithOffset(Events.Tag.allTags.toList) { (tag, offset) =>
    persistentEntityRegistry.eventStream(tag, offset)
      .collect {
        case EventStreamElement(_,ProductAdded(product), pOffset) =>
          log.debug(s"Writing product: ${product.name} to kafka")
          Product(product.id,product.name,product.quantity) -> pOffset
      }
  }

  def ref(id: String): PersistentEntityRef[ProductCommands[_]] = {
    persistentEntityRegistry
      .refFor[ProductEntity](id)
  }

}
