package app.server

import akka.actor.ActorRef
import akka.testkit.{TestActorRef, TestKit}
import app.adapters.database.{DbSpec, TaskDAO}
import app.models.Task
import app.services.TaskService
import org.joda.time.DateTime
import org.scalatest.{Matchers, WordSpec}
import play.api.libs.json.{JsNumber, JsArray, JsValue, JsString}
import spray.http.StatusCodes
import spray.httpx.PlayJsonSupport
import spray.testkit.ScalatestRouteTest

/**
 * User: asalvadore
 */
class TaskWebServiceSpec extends WordSpec with ScalatestRouteTest with Matchers with DbSpec with PlayJsonSupport {

  import dbProfile.profile.simple._

  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }


    val dao = new TaskDAO(dbProfile)
    val actorRef = TestActorRef(new TaskService(dao), "task-service")
    val taskWebService = new TaskWebService {
      override implicit def actorRefFactory = system
      override val taskService: ActorRef = actorRef
    }

    def loadTasks(tasks: List[Task]): Unit ={
      conn.withSession { implicit session =>
        dao.tasks ++= tasks
      }
    }


  "the taskWebService" should {
    "provide a method to retrieve all tasks" in  {
      loadTasks(List(
        Task(0, content = "c1", created = new DateTime(), finished = false, assignee = "a1"),
        Task(0, content = "c2", created = new DateTime(), finished = false, assignee = "a2")
      ))
      Get("/tasks") ~> taskWebService.taskServiceRoutes ~> check {
        status should be(StatusCodes.OK)
        (responseAs[JsValue] \ "tasks")(0) \ "content" should be(JsString("c1"))
      }
    }

    "provide a method to only get ids" in  {
      loadTasks(List(
        Task(0, content = "c1", created = new DateTime(), finished = false, assignee = "a1"),
        Task(0, content = "c2", created = new DateTime(), finished = false, assignee = "a2")
      ))

      Get("/tasks/ids") ~> taskWebService.taskServiceRoutes ~> check {
        status should be(StatusCodes.OK)
        (responseAs[JsValue] \ "ids") should be (JsArray(Seq(JsNumber(1),JsNumber(2))))
      }

    }


  }


}
