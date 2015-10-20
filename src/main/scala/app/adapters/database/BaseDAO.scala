package app.adapters.database

import app.adapters.database.BaseDAO.{Count, Ids, Result}
import app.adapters.database.support.DbProfile
import app.models.Identifiable
import scala.language.reflectiveCalls

/**
 * User: asalvadore
 */
trait BaseDAO {
  val dbProfile: DbProfile
  import dbProfile.profile.simple._

  type I <: Identifiable
  type T <: Table[I] {
    def id: Column[Long]
  }

  val table: TableQuery[T]
  

  def count(implicit session: Session): Count = {
    val count: Int = table.list.length
    Count(count)
  }

  def listIds(implicit session: Session): Ids = {
    val ids = table.list.map(_.id)
    Ids(ids)
  }

  def listAll(implicit session: Session): List[I] =
    table.list


  def add(element: I)(implicit session: Session): Result = {
    (table returning table.map(_.id)) += element match {
      case 0 => Result("Something went wrong", false)
      case n => Result(s"Task $n added successfully")
    }
  }

  def fetchById(id: Long)(implicit session: Session): Option[I] = {
    table.filter(_.id === id).firstOption
  }

  def deleteById(id: Long)(implicit session: Session): Result = {
    table.filter(_.id === id).delete match {
      case 0 => Result(s"Task $id was not found", false)
      case 1 => Result(s"Task $id successfully deleted", true)
      case _ => Result("Something went wrong", false)
    }
  }

  def updateById(id: Long, element: I)(implicit session: Session): Result = {
    table.filter(_.id === id).update(element) match {
      case 1 => Result(s"Task $id successfully modified")
      case _ => Result(s"Task $id was not found", false)
    }
  }

}

object BaseDAO {

  case class Count(number: Int)

  case class Ids(ids: List[Long])

  case class Result(message: String, successful: Boolean = true)

}
