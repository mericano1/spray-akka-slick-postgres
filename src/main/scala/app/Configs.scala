package app

import com.typesafe.config.ConfigFactory
import org.slf4j.{ Logger, LoggerFactory }

object Configs {
  val configuration = ConfigFactory.load()
  
  val interface    = configuration.getString("app.interface")
  val appPort      = configuration.getInt("app.port")

  val log: Logger = LoggerFactory.getLogger(this.getClass)
}