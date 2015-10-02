package app.models

import org.joda.time.DateTime


case class Task(
  taskId:   Long,
  content:  String,
  created:  DateTime,
  finished: Boolean,
  assignee: String
)







