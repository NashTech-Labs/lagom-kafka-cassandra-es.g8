package com.knoldus.lagomkafkacassandraes.api

import play.api.libs.json.{Format, Json}

/**
  *
  * @param id    - The product id
  * @param name  - The product name
  */
case class Product(id: String, name: String, quantity:Long)

object Product {

  implicit val format: Format[Product] = Json.format[Product]
}
