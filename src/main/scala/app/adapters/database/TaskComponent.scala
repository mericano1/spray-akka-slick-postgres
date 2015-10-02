package app.adapters.database

import app.adapters.database.support.DbProfile
import app.models.Task
import org.joda.time.DateTime
import com.github.tototoshi.slick.JdbcJodaSupport._

/**
 * User: asalvadore
 */
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
