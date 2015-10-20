package app.services

import akka.actor.Actor
import app.adapters.database.BaseDAO.{Count, Ids, Result}
import app.adapters.database.TaskDAO
import app.adapters.database.support.{DatabaseSupport, DbProfile}
import app.models.Task
import org.joda.time.DateTime
import play.api.libs.json.Json

object TaskService {
  case object FetchAll
  case class FetchAllResponse(tasks: List[Task])

  case class  CreateTask(content: String, assignee: String)
  case class  CreateTaskResponse(result: Result)

  case class  FetchTask(id: Int)
  case class  FetchTaskResponse(task: Option[Task])

  case class  ModifyTask(id: Int, content: String)
  case class  ModifyTaskResponse(result: Result)

  case class  DeleteTask(id: Int)
  case class  DeleteTaskResponse(result: Result)

  case object GetCount
  case class GetCountResponse(count: Count)

  case object GetIds
  case class GetIdsResponse(ids: List[Long])


  implicit val countFormat = Json.format[Count]
  implicit val idsFormat = Json.format[Ids]
  implicit val resultFormat = Json.format[Result]
  implicit val taskFormat = Json.format[Task]
  implicit val fetchAllResFormat = Json.format[FetchAllResponse]
  implicit val createTaskResFormat = Json.format[CreateTaskResponse]
  implicit val fetchTaskResFormat = Json.format[FetchTaskResponse]
  implicit val modifyTaskResFormat = Json.format[ModifyTaskResponse]
  implicit val deleteTaskResFormat = Json.format[DeleteTaskResponse]
  implicit val getCountResFormat = Json.format[GetCountResponse]
  implicit val getIdsResFormat = Json.format[GetIdsResponse]


}

class TaskService(val dao : TaskDAO) extends Actor with DatabaseSupport {
  import TaskService._

  override val dbProfile: DbProfile = dao.dbProfile

  def receive: Receive = {
    case FetchAll => db.withSession {implicit session =>
      sender ! FetchAllResponse(dao.listAll)
    }

    case CreateTask(content: String, assignee: String) => db.withSession { implicit session =>
      sender ! CreateTaskResponse(dao.add(Task(0, content, DateTime.now(), false, assignee)))
    }
    
    case FetchTask(id: Int) => db.withSession { implicit session =>
      sender ! FetchTaskResponse(dao.fetchById(id))
    }
    
    case ModifyTask(id: Int, content: String) => db.withSession { implicit session =>

      val result = dao.fetchById(id).map{task =>
        dao.updateById(id, Task(id, content, task.created, task.finished, task.assignee))
      }.getOrElse {
        Result(s"Task $id was not found", false)
      }

      sender ! ModifyTaskResponse(result)
    }
    
    case DeleteTask(id: Int) => db.withSession { implicit session =>
      sender ! DeleteTaskResponse(dao.deleteById(id))
    }

    case GetCount => db.withSession { implicit session =>
      sender ! GetCountResponse(dao.count)
    }
    
    case GetIds => db.withSession { implicit session =>
      sender ! GetIdsResponse(dao.listIds.ids)
    }

  }
}