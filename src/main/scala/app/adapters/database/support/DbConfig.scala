package app.adapters.database.support

import com.typesafe.config.Config

/**
 * User: asalvadore
 */
trait DbConfig {
  def dbURL: String
  def dbDriver: String
  def dbUser: String
  def dbPassword: String
  def maximumPoolSize: Int = 5
}

trait TypesafeDbConfig extends DbConfig{
  def conf: Config

  lazy val dbURL = conf.getString("db.url")
  lazy val dbDriver = conf.getString("db.driver")
  lazy val dbUser = conf.getString("db.username")
  lazy val dbPassword = conf.getString("db.password")
  override lazy val maximumPoolSize = conf.getInt("db.maximumPoolSize")
}
