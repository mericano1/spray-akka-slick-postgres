package app.adapters.database

import org.scalatest.{Matchers, WordSpec}

import scala.slick.jdbc.meta.MTable

/**
 * User: asalvadore
 */
class DbInitializerSpec extends WordSpec with Matchers with DbSpec {

  import dbProfile.profile.simple._


  "createTable" should {
    conn.withSession { implicit session =>
      "increases the number of tables in the database" in {
        tables.size should equal(1)
      }


      "create a table with the expected name" in {
        val tableNames = tables.map(_.name.name)
        tableNames should contain("tasks")
      }
    }
  }

  private def tables(implicit session: Session) = MTable.getTables("tasks").list

}
