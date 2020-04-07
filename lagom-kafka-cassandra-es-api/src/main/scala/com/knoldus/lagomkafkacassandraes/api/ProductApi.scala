package com.knoldus.lagomkafkacassandraes.api

import akka.{Done, NotUsed}
import com.lightbend.lagom.scaladsl.api.transport.Method
import com.lightbend.lagom.scaladsl.api.{Descriptor, Service, ServiceCall}

trait ProductApi extends Service {

  def getProductDetails(id: String): ServiceCall[NotUsed, String]

  def addProduct(): ServiceCall[Product, String]

  override final def descriptor: Descriptor = {
    import Service._

    named("product-api")
      .withCalls(
        restCall(Method.GET, "/api/details/get/:id", getProductDetails _),
        restCall(Method.POST, "/api/details/add/:id/:name/:quantity", addProduct _),
      ).withAutoAcl(true)
  }
}
