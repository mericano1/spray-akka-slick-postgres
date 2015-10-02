package app.adapters.database

import app.adapters.database.support.DbProfile
import app.models.Task
import org.joda.time.DateTime

/**
 * User: asalvadore
 */
class TaskDAO(val dbProfile: DbProfile) extends TaskComponent {

  import TaskDAO._
  import dbProfile.profile.simple._

  def numberOfTasks(implicit session: Session): Count = {
    val count: Int = tasks.list.length
    Count(count)
  }

  def listAllIds(implicit session: Session): Ids = {
    val ids = tasks.list.map(_.taskId)
    Ids(ids)
  }

  def listAllTasks(implicit session: Session): List[Task] =
    tasks.list


  def addTask(content: String, assignee: String)(implicit session: Session): Result = {
    val asTask = Task(0, content = content, created = new DateTime(), finished = false, assignee = assignee)
    (tasks returning tasks.map(_.taskId)) += asTask match {
      case 0 => Result("Something went wrong", false)
      case n => Result(s"Task $n added successfully")
    }
  }

  def fetchTaskById(id: Long)(implicit session: Session): Option[Task] = {
    tasks.filter(_.taskId === id).firstOption
  }

  def deleteTaskById(id: Long)(implicit session: Session): Result = {
    tasks.filter(_.taskId === id).delete match {
      case 0 => Result(s"Task $id was not found", false)
      case 1 => Result(s"Task $id successfully deleted", true)
      case _ => Result("Something went wrong", false)
    }
  }

  def updateTaskById(id: Long, newContent: String)(implicit session: Session): Result = {
    tasks.filter(_.taskId === id).
      map(t => t.content).update(newContent) match {
        case 1 => Result(s"Task $id successfully modified")
        case _ => Result(s"Task $id was not found", false)
      }
  }


  def deleteAll()(implicit session: Session): Result = {
    tasks.delete match {
      case 0 => Result("0 tasks deleted")
      case 1 => Result("1 task deleted")
      case n => Result(s"$n tasks deleted")
    }
  }
}

object TaskDAO {
  case class Count(numberOfTasks: Int)
  case class Ids(ids: List[Long])
  case class Result(message: String, successful: Boolean = true)
}