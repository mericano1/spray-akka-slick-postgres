package app.server

import java.lang.management.ManagementFactory
import java.util.concurrent.TimeUnit

import akka.actor._
import akka.event.Logging
import com.codahale.metrics.jvm._
import com.codahale.metrics.{JmxReporter, MetricRegistry}
import spray.http.HttpRequest
import spray.metrics.directives.CodaHaleMetricsDirectiveFactory
import spray.routing.directives.LogEntry

class ServerSupervisor(metricRegistry : MetricRegistry, val taskService: ActorRef) extends Actor
with TaskWebService {

  def actorRefFactory = context

  private val metricFactory = CodaHaleMetricsDirectiveFactory(metricRegistry)

  private val counterByUri = metricFactory.counter.all

  def receive = runRoute(
    logRequest((req: HttpRequest) => LogEntry(requestLogMessage(req), Logging.InfoLevel)) {
      pathPrefix("api" / "v1") {
        counterByUri.count {
          taskServiceRoutes
        }
      }
    }
  )

  def requestLogMessage(req: HttpRequest) = {
    s"method: ${req.method}, url: ${req.uri}, headers: ${req.headers}"
  }


}