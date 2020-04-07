package com.knoldus.lagomkafkacassandraes.impl.eventsourcing

import akka.Done
import com.knoldus.lagomkafkacassandraes.api.Product
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.commands.{AddProduct, GetProduct}
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.events.ProductAdded
import com.knoldus.lagomkafkacassandraes.impl.eventsourcing.state.ProductState
import com.knoldus.{Events, ProductCommands}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

class ProductEntity extends PersistentEntity {

  override type Command = ProductCommands[_]
  override type Event = Events
  override type State = ProductState

  override def initialState: ProductState = ProductState(None)

  /**
    * behavior is an abstract method that your concrete subclass must implement.
    * Behavior is a function from current State to Actions,which defines command and event handlers
    * Command handlers are invoked for incoming messages
    *
    * @return State=>Actions
    */
  override def behavior: ProductState => Actions = {
    case ProductState(_) => Actions()
      .onCommand[AddProduct, Done]
        {
          case (AddProduct(product), ctx, _) =>
            ctx.thenPersist(ProductAdded(product))(_ ⇒ ctx.reply(Done))
        }
      .onReadOnlyCommand[GetProduct, Product] {
        case (GetProduct(id), ctx, state) =>
          ctx.reply(state.product.getOrElse(Product(id, "name not found ", -1)))
      }
      .onEvent {
        case (ProductAdded(product), _) =>
          ProductState(Some(product))
      }

  }
}