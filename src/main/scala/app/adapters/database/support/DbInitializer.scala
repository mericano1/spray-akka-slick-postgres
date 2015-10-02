package app.adapters.database.support

import app.adapters.database.TaskComponent

import scala.slick.jdbc.meta.MTable

/**
 * User: asalvadore
 */
class DbInitializer(val dbProfile: DbProfile) extends TaskComponent with DatabaseSupport {

  import dbProfile.profile.simple._

  def initialize = {
    db.withTransaction { implicit session =>
      if (MTable.getTables("tasks").list.isEmpty) {
        createTable
      }
    }
  }

  val databaseTables = tasks

  def createTable(implicit session: Session) =
    databaseTables.ddl.create

  def dropTable(implicit session: Session) =
    databaseTables.ddl.drop

}
