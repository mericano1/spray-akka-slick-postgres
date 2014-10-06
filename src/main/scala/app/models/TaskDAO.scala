package app.models

import app.utils.PostgresSupport
import com.github.tototoshi.csv._
import org.joda.time.DateTime
import play.api.libs.json.Json
import com.github.tototoshi.slick.JdbcJodaSupport._

import scala.slick.driver.PostgresDriver.simple._

case class Task(
  taskId:   Long,
  content:  String,
  created:  DateTime,
  finished: Boolean,
  assignee: String
)

object TaskDAO extends PostgresSupport {

  class TaskTable(tag: Tag) extends Table[Task](tag, "tasks") {
    def taskId    = column[Long]     ("taskId", O.PrimaryKey)
    def content   = column[String]  ("content", O.DBType("VARCHAR(50)"), O.NotNull)
    def created   = column[DateTime]("created", O.DBType("TIMESTAMP"), O.NotNull)
    def finished  = column[Boolean] ("finished", O.DBType("BOOLEAN"), O.NotNull)
    def assignee  = column[String]  ("assignee", O.DBType("VARCHAR(20)"), O.NotNull)
    def *         = (taskId, content , created , finished , assignee) <> (Task.tupled, Task.unapply)
  }

  val tasks = TableQuery[TaskTable]
  implicit val jsonFormat = Json.format[Task]

  import Json._
  case class Count(numberOfTasks: Int)
  case class Ids(ids: List[Long])
  case class Result(result: String)
  implicit val countFmt = Json.format[Count]
  implicit val idsFmt = Json.format[Ids]
  implicit val resultFmt = Json.format[Result]
  def pgResult(result: String) = toJson(Result(result)).toString()

  def numberOfTasks: String = {
    val count: Int = tasks.list.length
    toJson(Count(count)).toString()
  }

  def listAllIds: String = {
    val ids = tasks.list.map(_.taskId)
    toJson(Ids(ids)).toString()
  }

  def listAllTasks: String =
    toJson(tasks.list).toString()

  def createTable() =
    tasks.ddl.create

  def dropTable() =
    tasks.ddl.drop

  def addTask(content: String, assignee: String): String = {
    val asTask = Task(tasks.list.length + 1, content = content, created = new DateTime(), finished = false, assignee = assignee)
    (tasks returning tasks.map(_.taskId)) += asTask match {
      case 0 => pgResult("Something went wrong")
      case n => pgResult(s"Task $n added successfully")
    }
  }

  def fetchTaskById(id: Long): String = {
    toJson(tasks.filter(_.taskId === id).list).toString()
  }

  def deleteTaskById(id: Long): String = {
    tasks.filter(_.taskId === id).delete match {
      case 0 => pgResult(s"Task $id was not found")
      case 1 => pgResult(s"Task $id successfully deleted")
      case _ => pgResult("Something went wrong")
    }
  }

  def updateTaskById(id: Long, newContent: String): String = {
    tasks.filter(_.taskId === id).
      map(t => t.content).
      update(newContent) match {
        case 1 => pgResult(s"Task $id successfully modified")
        case _ => pgResult(s"Task $id was not found")
      }
  }

  def addMultipleTasks(args: List[(String, String)]) = {
    args.map(arg => addTask(arg._1, arg._2)).map(result => println(result))
  }

  def populateTable(filename: String) = {
    val csvInfo = CSVConverter.convert(filename)
    addMultipleTasks(csvInfo)
  }

  def deleteAll() = {
    tasks.delete match {
      case 0 => pgResult("0 tasks deleted")
      case 1 => pgResult("1 task deleted")
      case n => pgResult(s"$n tasks deleted")
    }
  }
}

object CSVConverter {
  import java.io.File

import scala.collection.mutable.ListBuffer

  def convert(filename: String) = {
    val reader = CSVReader.open(new File(filename))
    val rawList = reader.iterator.toList
    val tweets = new ListBuffer[(String, String)]
    rawList.foreach(line => tweets ++= List((line(0), line(1))))
    tweets.toList
  }
}