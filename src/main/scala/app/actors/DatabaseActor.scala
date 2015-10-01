package app.actors

import akka.actor.Actor
import app.database.{DatabaseSupport, DbProfile}
import app.models.TaskDAO

object DatabaseActor {
  case object FetchAll
  case class  CreateTask(content: String, assignee: String)
  case class  FetchTask(id: Int)
  case class  ModifyTask(id: Int, content: String)
  case class  DeleteTask(id: Int)
  case object DeleteAll
  case object GetCount
  case class  Populate(file: String)
  case object GetIds
}

class DatabaseActor(val dao : TaskDAO) extends Actor with DatabaseSupport {
  import DatabaseActor._

  override val dbProfile: DbProfile = dao.dbProfile

  def receive: Receive = {
    case FetchAll => db.withSession {implicit session =>
      sender ! dao.listAllTasks
    }

    case CreateTask(content: String, assignee: String) => db.withSession { implicit session =>
      sender ! dao.addTask(content, assignee)
    }
    
    case FetchTask(id: Int) => db.withSession { implicit session =>
      sender ! dao.fetchTaskById(id)
    }
    
    case ModifyTask(id: Int, content: String) => db.withSession { implicit session =>
      sender ! dao.updateTaskById(id, content)
    }
    
    case DeleteTask(id: Int) => db.withSession { implicit session =>
      sender ! dao.deleteTaskById(id)
    }
    
    case DeleteAll => db.withSession { implicit session =>
      sender ! dao.deleteAll
    }
    
    case GetCount => db.withSession { implicit session =>
      sender ! dao.numberOfTasks
    }
    
    case GetIds => db.withSession { implicit session =>
      sender ! dao.listAllIds
    }

  }
}