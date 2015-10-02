package app.adapters.database.support

import scala.slick.driver.JdbcProfile

trait DbProfile {
  val profile: JdbcProfile
  val dbConfig: DbConfig
}
