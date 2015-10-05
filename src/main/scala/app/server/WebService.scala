package app.server

import akka.util.Timeout
import spray.routing.HttpService
import scala.language.postfixOps
import scala.concurrent.duration._

trait WebService extends HttpService {
  implicit def executionContext = actorRefFactory.dispatcher
  implicit val timeout = Timeout(120 seconds)
}