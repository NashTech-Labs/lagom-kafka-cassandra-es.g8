package com.knoldus.kafka

import akka.actor.ActorSystem
//import akka.kafka.ProducerSettings
//import akka.kafka.scaladsl.Producer
//import org.apache.kafka.common.serialization.{ByteArraySerializer, StringSerializer}
//
//trait CommonProducerFlow {
//
//  def producerFlow[Message](topic: Message)(implicit actorSystem: ActorSystem)={
//    val keySerializer: StringSerializer = new StringSerializer
//    val valueSerializerString: StringSerializer = new StringSerializer
//    val valueSerializerByte = new ByteArraySerializer
//
//    topic match {
//      case topic: String =>
//
//        val producerSettingsString: ProducerSettings[String, String] = ProducerSettings(actorSystem, keySerializer, valueSerializerString)
//        Producer.flexiFlow(producerSettingsString)
//      case _ =>
//        val producerSettingsByte: ProducerSettings[String, Array[Byte]] = ProducerSettings(actorSystem, keySerializer, valueSerializerByte)
//        Producer.flexiFlow(producerSettingsByte)
//    }
//
//  }
//}
