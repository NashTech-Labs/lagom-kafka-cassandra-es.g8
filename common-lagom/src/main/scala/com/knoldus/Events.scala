package com.knoldus

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventShards, AggregateEventTag, AggregateEventTagger}
import com.typesafe.config.{Config, ConfigFactory}

/**
  * A trait for events
  */
trait Events extends AggregateEvent[Events] {
  override def aggregateTag: AggregateEventTagger[Events] = Events.Tag
}

object Events {
  val config: Config = ConfigFactory.load

  val NumShards: Int = config.getInt("cassandra.numShards")
  val Tag: AggregateEventShards[Events] = AggregateEventTag.sharded[Events](NumShards)
}
