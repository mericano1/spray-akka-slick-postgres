package app.models

import org.joda.time.DateTime


case class Task (
  id:   Long,
  content:  String,
  created:  DateTime,
  finished: Boolean,
  assignee: String
) extends Identifiable







