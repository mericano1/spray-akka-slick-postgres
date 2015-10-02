package app.server

import akka.actor._
import akka.pattern.ask
import app.services.TaskService
import play.api.libs.json.JsObject
import spray.httpx.PlayJsonSupport

trait TaskWebService extends WebService with PlayJsonSupport {

  import TaskService._

  val dbWorker: ActorRef

  def postgresCall(message: Any) =
    (dbWorker ? message).mapTo[String]

  val taskServiceRoutes = {
    pathPrefix("tasks") {
      pathEndOrSingleSlash {
        get { ctx =>
          ctx.complete((dbWorker ? FetchAll).mapTo[FetchAllResponse])
        } ~
          post {
            entity(as[JsObject]) { js =>
              val content = (js \ "content").as[String]
              val assignee = (js \ "assignee").as[String]
              complete((dbWorker ? CreateTask(content, assignee)).mapTo[CreateTaskResponse])
            }
          }
      } ~
        path("count") {
          get { ctx =>
            ctx.complete((dbWorker ? GetCount).mapTo[GetCountResponse])
          }
        } ~
        path("all") {
          delete { ctx =>
            ctx.complete((dbWorker ? DeleteAll).mapTo[DeleteAllResponse])
          }
        } ~
        path("ids") {
          get { ctx =>
            ctx.complete((dbWorker ? GetIds).mapTo[GetIdsResponse])
          }
        }
    } ~
      path("task" / IntNumber) { taskId =>
        get { ctx =>
          ctx.complete((dbWorker ? FetchTask(taskId)).mapTo[FetchTaskResponse])
        } ~
          put {
            formFields('content.as[String]) { (content) =>
              complete((dbWorker ? ModifyTask(taskId, content)).mapTo[ModifyTaskResponse])
            }
          } ~
          delete { ctx =>
            ctx.complete((dbWorker ? DeleteTask(taskId)).mapTo[DeleteTaskResponse])
          }
      }
  }
}