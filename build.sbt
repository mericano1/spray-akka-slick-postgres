import Versions._

organization := "info.devinprogress"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-feature", "-encoding", "utf8")


libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "io.spray" %% "spray-client" % sprayVersion,
  "io.spray" %% "spray-can" % sprayVersion,
  "io.spray" %% "spray-httpx" % sprayVersion,
  "io.spray" %% "spray-routing" % sprayVersion,
  "com.typesafe.play" %% "play-json" % playJsonVersion,
  "com.typesafe.slick" %% "slick" % slickVersion,
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
  "joda-time" % "joda-time" % "2.4",
  "org.joda" % "joda-convert" % "1.6",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "ch.qos.logback" % "logback-classic" % "1.0.0",
  "com.zaxxer" % "HikariCP" % "2.4.1",
  "io.spray" %% "spray-testkit" % sprayVersion % "test",
  "com.h2database" % "h2" % "1.4.181" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)
