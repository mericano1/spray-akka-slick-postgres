package app.adapters.database

import app.adapters.database.support.{DbProfile, DbInitializer, DbConfig}
import org.scalatest.{BeforeAndAfterEachTestData, Suite, TestData}

import scala.slick.driver.JdbcDriver.simple._
import scala.slick.driver.{H2Driver, JdbcProfile}

trait DbSpec extends BeforeAndAfterEachTestData {
  this: Suite =>
  val connURL = "jdbc:h2:mem:usapi;DB_CLOSE_DELAY=-1;MODE=PostgreSQL;DATABASE_TO_UPPER=false"

  val datasourceDriver = "org.h2.jdbcx.JdbcDataSource"
  val conn = Database.forURL(connURL, driver = "org.h2.Driver")

  val databaseConfig = new DbConfig {
    override val dbDriver = null
    override val dbURL = connURL
    override val dbUser = ""
    override val dbPassword = ""
  }

  val dbProfile = new DbProfile {
    override val profile: JdbcProfile = H2Driver
    override val dbConfig: DbConfig = databaseConfig
  }

  val initializer = new DbInitializer(dbProfile)


  override def beforeEach(testData: TestData) {
    conn.withSession { implicit session =>
      initializer.createTable
    }
  }

  override def afterEach(testData: TestData): Unit = {
    conn.withSession { implicit session =>
      initializer.dropTable
    }
  }

}