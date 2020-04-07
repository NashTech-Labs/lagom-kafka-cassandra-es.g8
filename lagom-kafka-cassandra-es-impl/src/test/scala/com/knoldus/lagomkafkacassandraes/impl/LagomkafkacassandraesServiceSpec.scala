//package com.knoldus.lagomkafkacassandraes.impl
//
//import com.lightbend.lagom.scaladsl.server.LocalServiceLocator
//import com.lightbend.lagom.scaladsl.testkit.ServiceTest
//import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
//import com.knoldus.lagomkafkacassandraes.api._
//
//class LagomkafkacassandraesServiceSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {
//
//  private val server = ServiceTest.startServer(
//    ServiceTest.defaultSetup
//      .withCassandra()
//  ) { ctx =>
//    new LagomkafkacassandraesApplication(ctx) with LocalServiceLocator
//  }
//
//  val client: LagomkafkacassandraesService = server.serviceClient.implement[LagomkafkacassandraesService]
//
//  override protected def afterAll(): Unit = server.stop()
//
//  "lagom-kafka-cassandra-es service" should {
//
//    "say hello" in {
//      client.hello("Alice").invoke().map { answer =>
//        answer should ===("Hello, Alice!")
//      }
//    }
//
//    "allow responding with a custom message" in {
//      for {
//        _ <- client.useGreeting("Bob").invoke(GreetingMessage("Hi"))
//        answer <- client.hello("Bob").invoke()
//      } yield {
//        answer should ===("Hi, Bob!")
//      }
//    }
//  }
//}
