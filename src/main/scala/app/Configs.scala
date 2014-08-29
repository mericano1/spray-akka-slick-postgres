package app

import com.typesafe.config.ConfigFactory
import org.slf4j.{ Logger, LoggerFactory }

object Configs {
  val c = ConfigFactory.load()
  
  val interface    = c.getString("app.interface")
  val appPort      = c.getInt("app.port")

  val dbHost       = c.getString("db.host")
  val dbPort       = c.getInt("db.port")
  val dbDBName     = c.getString("db.dbname")
  val dbDriver     = c.getString("db.driver")
  val dbUser     = c.getString("db.username")
  val dbPassword     = c.getString("db.password")

  val log: Logger = LoggerFactory.getLogger(this.getClass)
}