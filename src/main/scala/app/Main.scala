package app

import akka.actor._
import akka.io.IO
import akka.routing.RoundRobinPool
import app.Configs._
import app.actors.DatabaseActor
import app.database.{DbConfig, DbInitializer, DbProfile, TypesafeDbConfig}
import app.models.TaskDAO
import app.server.ServerSupervisor
import app.utils.ShutdownHook
import com.typesafe.config.Config
import spray.can.Http

import scala.slick.driver.{JdbcProfile, PostgresDriver}

object Main extends App with ShutdownHook{
  implicit val system = ActorSystem("main-system")
  log.info("Actor system $system is up and running")

  private val dbProfile = new DbProfile {
    override val profile: JdbcProfile = PostgresDriver
    override val dbConfig: DbConfig = new TypesafeDbConfig {
      override def conf: Config = configuration
    }
  }
  private val taskDao = new TaskDAO(dbProfile)
  new DbInitializer(dbProfile).initialize
  private val dbActor = system.actorOf(Props(new DatabaseActor(taskDao)), "database-actor")


  log.info("Postgres is up and running")

  private val mainHandler = system.actorOf(
    Props(new ServerSupervisor(dbActor))
      .withRouter(RoundRobinPool(nrOfInstances = 10)), "main-http-actor"
  )

  IO(Http) ! Http.Bind(mainHandler, interface = Configs.interface, port = Configs.appPort)


}