package app.models

import app.models.TaskDAO.{addTask, createTable, dropTable, numberOfTasks}
import app.{Configs => C}
import org.scalatest.{BeforeAndAfter, Matchers, WordSpec}
import play.api.libs.json.{JsNumber, JsObject, JsString}
import scala.slick.driver.PostgresDriver.simple.{Database, Session}
import scala.slick.jdbc.meta.MTable

class TaskDAOSpec extends WordSpec with Matchers with BeforeAndAfter {

  private implicit var session: Session = _

  before {
    session = Database.forURL(
      url = s"jdbc:postgresql://${C.dbHost}:${C.dbPort}/${C.dbDBName}",
      driver = C.dbDriver
    ).
      createSession()
    if(tables.size > 0) dropTable()
  }

  "createTable" should {
    "increase the number of tables in the database" in {
      createTable()

      tables.size should equal(1)
    }

    "add a table with the expected name" in {
      createTable()

      val tableNames = tables.map(_.name.name)
      tableNames should contain("tasks")
    }
  }

  "addTask" should {
    "return success message that task was added" in {
      val expected = JsObject(fields = List(("result", JsString("Task 1 added successfully")))).toString()
      createTable()

      val response = addTask(content = content, assignee = assignee)

      response should equal(expected)
    }

    "increase the number of rows in the table" in {
      createTable()

      addTask(content = content, assignee = assignee)

      tables.length should equal(1)
    }
  }

  "numberOfTasks" should {
    "return json message with 0 for empty table" in {
      createTable()

      val expected = JsObject(fields = List(("numberOfTasks", JsNumber(0)))).toString()
      numberOfTasks should equal(expected)
    }

    "return json message with 1 when there has been 1 insert" in {
      createTable()

      addTask(content = content, assignee = assignee)

      val expected = JsObject(fields = List(("numberOfTasks", JsNumber(1)))).toString()
      numberOfTasks should equal(expected)
    }

    "return json message with 3 when there have been 3 inserts" in {
      createTable()

      addTask(content = s"$content insert 1", assignee = assignee)
      addTask(content = s"$content insert 2", assignee = assignee)
      addTask(content = s"$content insert 3", assignee = assignee)

      val expected = JsObject(fields = List(("numberOfTasks", JsNumber(3)))).toString()
      numberOfTasks should equal(expected)
    }
  }

  after {
    session.close()
  }

  private val content = "stub content"
  private val assignee = "stub assignee"
  private def tables(implicit session : scala.slick.jdbc.JdbcBackend#SessionDef) = MTable.getTables("tasks").list
}