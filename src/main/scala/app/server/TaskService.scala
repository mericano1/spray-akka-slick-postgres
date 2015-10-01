package app.server

import akka.actor._
import akka.pattern.ask
import app.actors.DatabaseActor
import play.api.libs.json.JsObject
import spray.httpx.PlayJsonSupport

trait TaskService extends WebService with PlayJsonSupport {
  import app.actors.DatabaseActor._

  val databaseWorker : ActorRef
  
  def postgresCall(message: Any) =
    (databaseWorker ? message).mapTo[String]

  val taskServiceRoutes = {
    pathPrefix("tasks") {
      path("") {
        get { ctx =>
          ctx.complete(postgresCall(FetchAll))
        } ~
          post {
            entity(as[JsObject]) { js =>
              complete(postgresCall(CreateTask((js \ "content").as[String], (js \ "assignee").as[String])))
            }
          }
      } ~
      path("count") {
        get { ctx =>
          ctx.complete(postgresCall(GetCount))
        }
      } ~
      path("all") {
        delete { ctx =>
          ctx.complete(postgresCall(DeleteAll))
        }
      } ~
      path("populate" / Segment) { filename =>
        post { ctx =>
          complete(postgresCall(Populate(filename)))
        }
      } ~
      path("ids") {
        get { ctx =>
          ctx.complete(postgresCall(GetIds))
        }
      }
    } ~
    path("task" / IntNumber) { taskId =>
      get { ctx =>
        ctx.complete(postgresCall(FetchTask(taskId)))
      } ~
        put {
          formFields('content.as[String]) { (content) =>
            complete(postgresCall(ModifyTask(taskId, content)))
          }
        } ~
          delete { ctx =>
            ctx.complete(postgresCall(DeleteTask(taskId)))
          }
    }
  }
}