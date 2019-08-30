package com.codingkapoor.employee.persistence.write

import java.time.LocalDate

import com.lightbend.lagom.scaladsl.persistence.{AggregateEvent, AggregateEventTag}
import play.api.libs.json.{Format, Json}

object EmployeeEvent {
  val Tag: AggregateEventTag[EmployeeEvent] = AggregateEventTag[EmployeeEvent]
}

sealed trait EmployeeEvent extends AggregateEvent[EmployeeEvent] {
  def aggregateTag: AggregateEventTag[EmployeeEvent] = EmployeeEvent.Tag
}

case class EmployeeAdded(id: String, name: String, gender: String, doj: LocalDate, pfn: String) extends EmployeeEvent

object EmployeeAdded {
  implicit val format: Format[EmployeeAdded] = Json.format[EmployeeAdded]
}

case class EmployeeUpdated(id: String, name: String, gender: String, doj: LocalDate, pfn: String) extends EmployeeEvent

object EmployeeUpdated {
  implicit val format: Format[EmployeeUpdated] = Json.format[EmployeeUpdated]
}
