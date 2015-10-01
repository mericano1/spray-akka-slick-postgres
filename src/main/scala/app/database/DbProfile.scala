package app.database

import slick.driver.JdbcProfile

trait DbProfile {
  val profile: JdbcProfile
  val dbConfig: DbConfig
}
