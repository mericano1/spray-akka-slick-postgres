package app.server

import akka.actor._

class ServerSupervisor(val databaseWorker: ActorRef) extends Actor
  with TaskService
{

  def actorRefFactory = context
  def receive = runRoute(
    pathPrefix("api" / "v1") {
      taskServiceRoutes
    }
  )
}