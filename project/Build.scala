import sbt._
import Keys._

object BuildSettings {
  val buildOrganization = "spray-akka-slick-postgres"
  val buildVersion      = "0.1.0"
  val buildScalaVersion = "2.10.4"

  val buildSettings = Defaults.coreDefaultSettings ++ Seq (
    organization := buildOrganization,
    version      := buildVersion,
    scalaVersion := buildScalaVersion
  )

  scalacOptions ++= Seq(
    "-unchecked",
    "-deprecation",
    "-feature",
    "-encoding", "utf8"
  )
}

object Resolvers {
  val sprayRepo       = "spray"                  at "http://repo.spray.io/"
  val sprayNightlies  = "Spray Nightlies"        at "http://nightlies.spray.io/"
  val sonatypeRel     = "Sonatype OSS Releases"  at "http://oss.sonatype.org/content/repositories/releases/"
  val sonatypeSnap    = "Sonatype OSS Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

  val sprayResolvers    = Seq(sprayRepo, sprayNightlies)
  val sonatypeResolvers = Seq(sonatypeRel, sonatypeSnap)
  val allResolvers      = sprayResolvers ++ sonatypeResolvers
}

object Dependencies {
  val akkaVersion  = "2.3.6"
  val sprayVersion = "1.3.1"

  val akkaActor    = "com.typesafe.akka" %% "akka-actor" % akkaVersion
  val akkaSlf4j    = "com.typesafe.akka" %% "akka-slf4j" % akkaVersion
  val sprayCan     = "io.spray" %  "spray-can"     % sprayVersion
  val sprayHttpx   = "io.spray" %  "spray-httpx"   % sprayVersion
  val sprayRouting = "io.spray" %  "spray-routing" % sprayVersion
  val playJson    = "com.typesafe.play" %% "play-json" % "2.4.0-M1"
  val slick        = "com.typesafe.slick"   %% "slick"             % "2.1.0"
  val postgres     = "org.postgresql"           %  "postgresql"        % "9.3-1102-jdbc41"
  val slickJoda    =  Seq(
    "joda-time" % "joda-time" % "2.5",
    "org.joda" % "joda-convert" % "1.7",
    "com.github.tototoshi" %% "slick-joda-mapper" % "1.2.0"
  )
  val scalaCsv     = "com.github.tototoshi" %% "scala-csv"         % "1.0.0"
  val logback      = "ch.qos.logback"       %  "logback-classic"   % "1.1.2"
  val scalatest = "org.scalatest" %% "scalatest" % "2.2.2" % "test"
}

object AppBuild extends Build {
  import Resolvers._
  import Dependencies._
  import BuildSettings._

  val akkaDeps = Seq(akkaActor, akkaSlf4j)

  val sprayDeps = Seq(
    sprayCan,
    sprayHttpx,
    sprayRouting,
    playJson
  )

  val otherDeps = Seq(
    slick,
    postgres,
    scalaCsv,
    logback,
    scalatest
  ) ++ slickJoda

  val allDeps = akkaDeps ++ sprayDeps ++ otherDeps

  lazy val mainProject = Project(
    "spray-akka-slick-postgres",
    file("."),
    settings = buildSettings ++ Seq(resolvers           ++= allResolvers,
      libraryDependencies ++= allDeps)
  )
}