package com.knoldus
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType

/**
  * a trait for Commands
  * @tparam R reply type
  */
trait ProductCommands[R] extends ReplyType[R]