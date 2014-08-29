package app.actors

import akka.actor._
import akka.io.IO
import akka.routing.RoundRobinRouter
import app.server.ServerSupervisor
import app.Configs
import spray.can.Http

object Starter {
  case object Start
  case object Stop
}

class Starter extends Actor {
  import app.actors.Starter.Start

  implicit val system = context.system

  def receive: Receive = {
    case Start =>
      val mainHandler: ActorRef =
        context.actorOf(Props[ServerSupervisor].withRouter(RoundRobinRouter(nrOfInstances = 10)))
      IO(Http) ! Http.Bind(mainHandler, interface = Configs.interface, port = Configs.appPort)
  }
}