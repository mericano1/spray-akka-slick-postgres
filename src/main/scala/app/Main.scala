package app

import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit

import akka.actor.Status.Failure
import akka.actor._
import akka.io.IO
import akka.routing.RoundRobinPool
import app.Configs._
import app.adapters.database._
import app.adapters.database.support.{DbConfig, DbInitializer, DbProfile, TypesafeDbConfig}
import app.server.ServerSupervisor
import app.services.TaskService
import app.utils.ShutdownHook
import com.codahale.metrics.health.jvm.ThreadDeadlockHealthCheck
import com.codahale.metrics.health.{HealthCheck, HealthCheckRegistry}
import com.codahale.metrics.jvm.{BufferPoolMetricSet, GarbageCollectorMetricSet, MemoryUsageGaugeSet, ThreadStatesGaugeSet}
import com.codahale.metrics.{JmxReporter, MetricRegistry}
import com.typesafe.config.Config
import spray.can.Http

import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.slick.driver.{JdbcProfile, PostgresDriver}

object Main extends App with ShutdownHook{
  implicit val system = ActorSystem("main-system")
  log.info("Actor system $system is up and running")
  private implicit val configuration: Config = Configs.configuration


  private val metricsRegistry = new MetricRegistry
  private val healthCheckRegistry = new HealthCheckRegistry
  private val dbProfile = createDbProfile
  private val taskDao = new TaskDAO(dbProfile)
  private val dbActor = system.actorOf(Props(new TaskService(taskDao){
    override val maybeMetricsRegistry: Option[AnyRef] = Some(metricsRegistry)
    override val maybeHealthCheckRegistry: Option[AnyRef] = Some(healthCheckRegistry)
  }), "database-actor")
  private val mainHandler = system.actorOf(
    Props(new ServerSupervisor(metricsRegistry, dbActor))
      .withRouter(RoundRobinPool(nrOfInstances = 10)), "main-http-actor"
  )


  addDeadLetterLogger(system)
  initDatabase(dbProfile)
  log.info("Postgres is up and running")

  startReporters()
  log.info("Metrics started")


  IO(Http) ! Http.Bind(mainHandler, interface = Configs.interface, port = Configs.appPort)


  private def startReporters() {
    metricsRegistry.registerAll(new ThreadStatesGaugeSet())
    metricsRegistry.registerAll(new GarbageCollectorMetricSet())
    metricsRegistry.registerAll(new BufferPoolMetricSet(ManagementFactory.getPlatformMBeanServer()))
    metricsRegistry.registerAll(new MemoryUsageGaugeSet())
    val reporter = JmxReporter.forRegistry(metricsRegistry)
      .convertRatesTo(TimeUnit.SECONDS)
      .convertDurationsTo(TimeUnit.MILLISECONDS)
      .build()
    reporter.start()
    healthCheckRegistry.register("deadlocks", new ThreadDeadlockHealthCheck())
    logHealth(healthCheckRegistry)
  }

  def logHealth(registry: HealthCheckRegistry)(implicit system :ActorSystem, config: Config): Unit = {
    import system.dispatcher
    val refreshInterval = config.getLong("app.healthchecks.reporting.interval.seconds")
    system.scheduler.schedule(FiniteDuration(refreshInterval, "seconds"), FiniteDuration(refreshInterval, "seconds")){
      registry.runHealthChecks().asScala.foreach{
        case (name: String, result: HealthCheck.Result) => log.info(s"The healthCheck $name reported status was $result")
      }
    }
  }

  private def initDatabase(dbProfile: DbProfile): Unit = {
    new DbInitializer(dbProfile).initialize
  }

  private def createDbProfile(implicit configuration: Config): DbProfile =  new DbProfile {
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