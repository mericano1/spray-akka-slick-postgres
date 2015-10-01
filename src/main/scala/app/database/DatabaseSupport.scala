package app.database

import java.sql.Connection
import javax.sql.DataSource

import com.zaxxer.hikari.HikariDataSource

import scala.slick.driver.JdbcDriver.simple._


/**
 * User: asalvadore
 */
trait DatabaseSupport {

  val dbProfile: DbProfile

  private val dataSource : DataSource = {
    val ds = new HikariDataSource()
    val dbConfig = dbProfile.dbConfig
    ds.setMaximumPoolSize(10)
    ds.setUsername(dbConfig.dbUser)
    ds.setPassword(dbConfig.dbPassword)
    ds.setJdbcUrl(dbConfig.dbURL)
    ds.setDriverClassName(dbConfig.dbDriver)
    ds.setMaximumPoolSize(dbConfig.maximumPoolSize)
    ds
  }

  val db = Database.forDataSource(dataSource)


  def inSerializableTransaction[T](block: Session => T) = db.withSession { implicit session =>
    require(session.conn.getAutoCommit, "A transaction has already been started")
    val oldLevel = session.conn.getTransactionIsolation
    session.conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE)
    val r = session.withTransaction(block(session))
    session.conn.setTransactionIsolation(oldLevel)
    r
  }


}


