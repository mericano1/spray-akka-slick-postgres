package app.adapters.database

import app.adapters.database.TaskDAO.{Count, Result}
import org.scalatest.{Matchers, WordSpec}

import scala.slick.jdbc.meta.MTable

class TaskDAOSpec extends WordSpec with Matchers with DbSpec {

  import dbProfile.profile.simple._

  val taskDAO = new TaskDAO(dbProfile)

  "addTask" should {
    "return success message that task was added" in conn.withSession { implicit session =>
      val expected = Result("Task 1 added successfully")

      val response = taskDAO.addTask(content = content, assignee = assignee)

      response should equal(expected)
    }

    "increase the number of rows in the table" in conn.withSession { implicit session =>

      taskDAO.addTask(content = content, assignee = assignee)

      tables.length should equal(1)
    }
  }

  "numberOfTasks" should {
    "return json message with 0 for empty table" in conn.withSession { implicit session =>

      val expected = Count(0)
      taskDAO.numberOfTasks should equal(expected)
    }

    "return json message with 1 when there has been 1 insert" in conn.withSession { implicit session =>

      taskDAO.addTask(content = content, assignee = assignee)

      val expected =  Count(1)
      taskDAO.numberOfTasks should equal(expected)
    }

    "return json message with 3 when there have been 3 inserts" in conn.withSession { implicit session =>

      taskDAO.addTask(content = s"$content insert 1", assignee = assignee)
      taskDAO.addTask(content = s"$content insert 2", assignee = assignee)
      taskDAO.addTask(content = s"$content insert 3", assignee = assignee)

      val expected =  Count(3)
      taskDAO.numberOfTasks should equal(expected)
    }
  }


  private val content = "stub content"
  private val assignee = "stub assignee"

  private def tables(implicit session: Session) = MTable.getTables("tasks").list
}