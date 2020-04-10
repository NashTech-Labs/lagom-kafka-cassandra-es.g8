organization in ThisBuild := "com.knoldus"
version in ThisBuild := "1.0-SNAPSHOT"

scalaVersion in ThisBuild := "2.13.0"

val macwire = "com.softwaremill.macwire" %% "macros" % "2.3.3" % "provided"
val scalaTest = "org.scalatest" %% "scalatest" % "3.1.1" % Test
val alpakkaElastic = "com.lightbend.akka" %% "akka-stream-alpakka-elasticsearch" % "2.0.0-RC2"
val alpakkaKafka = "com.typesafe.akka" %% "akka-stream-kafka" % "2.0.2"

val commonLagomAPISettings = libraryDependencies ++= Seq(
  lagomScaladslApi
)

val commonLagomImplSettings = libraryDependencies ++= Seq(
  lagomScaladslPersistenceCassandra,
  lagomScaladslKafkaBroker,
  lagomScaladslTestKit,
  macwire,
  scalaTest,
  alpakkaElastic,
  alpakkaKafka
)


lazy val `lagom-kafka-cassandra-es` = (project in file("."))
  .aggregate(`lagom-kafka-cassandra-es-api`, `lagom-kafka-cassandra-es-impl`, `common-lagom`)

lazy val `common-lagom` = (project in file("common-lagom"))
  .settings(commonLagomAPISettings: _*)
  .settings(commonLagomImplSettings: _*)

lazy val `lagom-kafka-cassandra-es-api` = (project in file("lagom-kafka-cassandra-es-api"))
  .settings(commonLagomAPISettings: _*)
  .dependsOn(`common-lagom`)

lazy val `lagom-kafka-cassandra-es-impl` = (project in file("lagom-kafka-cassandra-es-impl"))
  .enablePlugins(LagomScala)
  .settings(commonLagomImplSettings: _*)
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`lagom-kafka-cassandra-es-api`)
  .dependsOn(`common-lagom`)

lagomCassandraEnabled in ThisBuild := false
lagomUnmanagedServices in ThisBuild := Map("cas_native" -> "http://localhost:9042")
lagomKafkaEnabled in ThisBuild := false
lagomUnmanagedServices in ThisBuild += ("elastic-search" -> "http://127.0.0.1:9200")