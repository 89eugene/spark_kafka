name := "Spark_Kafka"

version := "0.1"

scalaVersion := "2.12.12"

val circeVersion = "0.15.0-M1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
).map(_ % circeVersion)

libraryDependencies +="org.apache.commons" % "commons-csv" % "1.9.0"

libraryDependencies += "org.apache.kafka" % "kafka-clients" % "0.10.1.0"


