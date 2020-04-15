package com.knoldus
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.ReplyType
trait ProductCommands[R] extends ReplyType[R]