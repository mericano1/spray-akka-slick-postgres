import Versions._

organization := "info.devinprogress"

scalaVersion := "2.11.6"

scalacOptions := Seq("-unchecked", "-feature", "-encoding", "utf8")

resolvers += "asalvadore maven bintray" at "http://dl.bintray.com/asalvadore/maven"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "io.spray" %% "spray-client" % sprayVersion,
  "io.spray" %% "spray-can" % sprayVersion,
  "io.spray" %% "spray-httpx" % sprayVersion,
  "io.spray" %% "spray-routing" % sprayVersion,
  "info.devinprogress" %% "spray-metrics" % ("0." + sprayVersion),
  "com.typesafe.play" %% "play-json" % playJsonVersion,
  "com.typesafe.slick" %% "slick" % slickVersion,
  "org.postgresql" % "postgresql" % "9.4-1200-jdbc41",
  "joda-time" % "joda-time" % "2.4",
  "org.joda" % "joda-convert" % "1.6",
  "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0",
  "ch.qos.logback" % "logback-classic" % "1.0.0",
  "com.zaxxer" % "HikariCP" % "2.4.1",
  "io.dropwizard.metrics" % "metrics-jvm" % "3.1.2",
  "io.dropwizard.metrics" % "metrics-healthchecks" % "3.1.2",
  "io.spray" %% "spray-testkit" % sprayVersion % "test",
  "com.h2database" % "h2" % "1.4.181" % "test",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)

enablePlugins(JavaServerAppPackaging)

enablePlugins(JDebPackaging)

name in Debian := name.value

version in Debian := version.value

maintainer in Linux := "Tech Team <tech@devinprogress.info>"

packageDescription in Linux := "templateweb application"

packageSummary in Linux := "template web application"


target in Debian <<= Keys.target apply ((t) => t / (name + "_" + version))

bashScriptConfigLocation := Some("${app_home}/../conf/jvmopts")

// This is copying all the conf folders under the single-deploy conf
mappings in Universal <++= baseDirectory map {  base => {
  val folders = Seq (
    ("", base / "src/main/resources")
  )
  val dirAndFiles = folders.map(dir => (dir._1, dir._2, getFileTree(dir._2)))
  dirAndFiles.flatMap { case (destPath, dir, files) => files.map { f =>
    val relativePath = dir.toURI.relativize(f.toURI).getPath
    (f, s"conf/$destPath/$relativePath")
  }}
}}
