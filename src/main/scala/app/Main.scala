package app

import akka.actor.Status.Failure
import akka.actor._
import akka.io.IO
import akka.routing.RoundRobinPool
import app.Configs._
import app.adapters.database._
import app.adapters.database.support.{DbProfile, DbInitializer, TypesafeDbConfig, DbConfig}
import app.server.ServerSupervisor
import app.services.TaskService
import app.utils.ShutdownHook
import com.typesafe.config.Config
import spray.can.Http

import scala.slick.driver.{JdbcProfile, PostgresDriver}

object Main extends App with ShutdownHook{
  implicit val system = ActorSystem("main-system")
  log.info("Actor system $system is up and running")


  private val dbProfile = createDbProfile(Configs.configuration)
  private val taskDao = new TaskDAO(dbProfile)
  private val dbActor = system.actorOf(Props(new TaskService(taskDao)), "database-actor")
  private val mainHandler = system.actorOf(
    Props(new ServerSupervisor(dbActor))
      .withRouter(RoundRobinPool(nrOfInstances = 10)), "main-http-actor"
  )


  addDeadLetterLogger(system)
  initDatabase(dbProfile)
  log.info("Postgres is up and running")


  IO(Http) ! Http.Bind(mainHandler, interface = Configs.interface, port = Configs.appPort)


  private def initDatabase(dbProfile: DbProfile): Unit = {
    new DbInitializer(dbProfile).initialize
  }

  private def createDbProfile(configuration: Config): DbProfile =  new DbProfile {
    override val profile: JdbcProfile = PostgresDriver
    override val dbConfig: DbConfig = new TypesafeDbConfig {
      override def conf: Config = configuration
    }
  }


  private def addDeadLetterLogger(system: ActorSystem): Boolean = {
    val loggingActor = system.actorOf(Props(new Actor with ActorLogging {
      override def receive: Receive = {
        case DeadLetter(Failure(ex), sender, recipient) => log.error(ex, s"Failure in the communication between $sender and $recipient")
      }
    }))

    system.eventStream.subscribe(loggingActor, classOf[DeadLetter])
  }
}