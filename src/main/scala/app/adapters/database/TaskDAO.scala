package app.adapters.database

import app.adapters.database.support.DbProfile
import app.models.Task

/**
 * User: asalvadore
 */
class TaskDAO(val dbProfile: DbProfile) extends BaseDAO with TaskComponent {

  override type I = Task
  override type T = TaskTable
  override val table: dbProfile.profile.simple.TableQuery[T] = tasks



}
