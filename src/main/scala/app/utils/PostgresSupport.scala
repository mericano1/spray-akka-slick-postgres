package app.utils

import app.models.TaskDAO
import app.{Configs => C}

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.jdbc.meta.MTable

trait PostgresSupport {
  def db = Database.forURL(
    url = s"jdbc:postgresql://${C.dbHost}:${C.dbPort}/${C.dbDBName}",
    user = C.dbUser,
    password = C.dbPassword,
    driver = C.dbDriver
  )

  implicit val session: Session = db.createSession()

  def startPostgres() = {
    if (MTable.getTables("tasks").list.isEmpty) {
      TaskDAO.createTable
    }
  }
}