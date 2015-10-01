package app.models

import app.database.DbProfile
import com.github.tototoshi.slick.JdbcJodaSupport._
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.libs.json.Json._


case class Task(
  taskId:   Long,
  content:  String,
  created:  DateTime,
  finished: Boolean,
  assignee: String
)

case class Count(numberOfTasks: Int)
case class Ids(ids: List[Long])
case class Result(result: String)



trait TaskComponent {
  val dbProfile: DbProfile

  import dbProfile.profile.simple._


  class TaskTable(tag: Tag) extends Table[Task](tag, "tasks") {
    def taskId    = column[Long]     ("taskId", O.AutoInc, O.PrimaryKey, O.DBType("BIGSERIAL"))
    def content   = column[String]  ("content", O.DBType("VARCHAR(50)"), O.NotNull)
    def created   = column[DateTime]("created", O.DBType("TIMESTAMP"), O.NotNull)
    def finished  = column[Boolean] ("finished", O.DBType("BOOLEAN"), O.NotNull)
    def assignee  = column[String]  ("assignee", O.DBType("VARCHAR(20)"), O.NotNull)
    def *         = (taskId, content , created , finished , assignee) <> (Task.tupled, Task.unapply)
  }

  val tasks = TableQuery[TaskTable]

}

class TaskDAO(val dbProfile: DbProfile) extends TaskComponent {

  import dbProfile.profile.simple._
  implicit val jsonFormat = Json.format[Task]
  implicit val countFmt = Json.format[Count]
  implicit val idsFmt = Json.format[Ids]
  implicit val resultFmt = Json.format[Result]

  def pgResult(result: String) = toJson(Result(result)).toString()

  def numberOfTasks(implicit session: Session): String = {
    val count: Int = tasks.list.length
    toJson(Count(count)).toString()
  }

  def listAllIds(implicit session: Session): String = {
    val ids = tasks.list.map(_.taskId)
    toJson(Ids(ids)).toString()
  }

  def listAllTasks(implicit session: Session): String =
    toJson(tasks.list).toString()


  def addTask(content: String, assignee: String)(implicit session: Session): String = {
    val asTask = Task(tasks.list.length + 1, content = content, created = new DateTime(), finished = false, assignee = assignee)
    (tasks returning tasks.map(_.taskId)) += asTask match {
      case 0 => pgResult("Something went wrong")
      case n => pgResult(s"Task $n added successfully")
    }
  }

  def fetchTaskById(id: Long)(implicit session: Session): String = {
    toJson(tasks.filter(_.taskId === id).list).toString()
  }

  def deleteTaskById(id: Long)(implicit session: Session): String = {
    tasks.filter(_.taskId === id).delete match {
      case 0 => pgResult(s"Task $id was not found")
      case 1 => pgResult(s"Task $id successfully deleted")
      case _ => pgResult("Something went wrong")
    }
  }

  def updateTaskById(id: Long, newContent: String)(implicit session: Session): String = {
    tasks.filter(_.taskId === id).
      map(t => t.content).
      update(newContent) match {
        case 1 => pgResult(s"Task $id successfully modified")
        case _ => pgResult(s"Task $id was not found")
      }
  }


  def deleteAll()(implicit session: Session) = {
    tasks.delete match {
      case 0 => pgResult("0 tasks deleted")
      case 1 => pgResult("1 task deleted")
      case n => pgResult(s"$n tasks deleted")
    }
  }
}
