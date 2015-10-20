package app.server

import akka.actor._
import akka.pattern.ask
import app.services.TaskService
import play.api.libs.json.JsObject
import spray.httpx.PlayJsonSupport

trait TaskWebService extends WebService with PlayJsonSupport {

  import TaskService._

  val taskService: ActorRef

  val taskServiceRoutes = {
    pathPrefix("tasks") {
      pathEndOrSingleSlash {
        get { ctx =>
          ctx.complete((taskService ? FetchAll).mapTo[FetchAllResponse])
        } ~
          post {
            entity(as[JsObject]) { js =>
              val content = (js \ "content").as[String]
              val assignee = (js \ "assignee").as[String]
              complete((taskService ? CreateTask(content, assignee)).mapTo[CreateTaskResponse])
            }
          }
      } ~
        path("count") {
          get { ctx =>
            ctx.complete((taskService ? GetCount).mapTo[GetCountResponse])
          }
        } ~
        path("ids") {
          get { ctx =>
            ctx.complete((taskService ? GetIds).mapTo[GetIdsResponse])
          }
        }
    } ~
      path("task" / IntNumber) { taskId =>
        get { ctx =>
          ctx.complete((taskService ? FetchTask(taskId)).mapTo[FetchTaskResponse])
        } ~
          put {
            formFields('content.as[String]) { (content) =>
              complete((taskService ? ModifyTask(taskId, content)).mapTo[ModifyTaskResponse])
            }
          } ~
          delete { ctx =>
            ctx.complete((taskService ? DeleteTask(taskId)).mapTo[DeleteTaskResponse])
          }
      }
  }
}