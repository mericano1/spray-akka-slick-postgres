package app.server

import akka.actor._
import akka.event.Logging
import spray.http.HttpRequest
import spray.routing.directives.LogEntry

class ServerSupervisor(val taskService: ActorRef) extends Actor
with TaskWebService {

  def actorRefFactory = context

  def receive = runRoute(
    logRequest((req: HttpRequest) => LogEntry(requestLogMessage(req), Logging.InfoLevel)) {
      pathPrefix("api" / "v1") {
        taskServiceRoutes
      }
    }
  )

  def requestLogMessage(req: HttpRequest) = {
    s"method: ${req.method}, url: ${req.uri}, headers: ${req.headers}"
  }
}