package com.codingkapoor.employee.api

import java.time.LocalDate

import julienrf.json.derived
import play.api.libs.json._

sealed trait EmployeeKafkaEvent {
  val id: String
}

object EmployeeKafkaEvent {
  implicit val format: Format[EmployeeKafkaEvent] = derived.flat.oformat((__ \ "type").format[String])
}

case class EmployeeAddedKafkaEvent(id: String, name: String, gender: String, doj: LocalDate, pfn: String) extends EmployeeKafkaEvent

object EmployeeAddedKafkaEvent {
  implicit val format: Format[EmployeeAddedKafkaEvent] = Json.format[EmployeeAddedKafkaEvent]
}

case class EmployeeUpdatedKafkaEvent(id: String, name: String, gender: String, doj: LocalDate, pfn: String) extends EmployeeKafkaEvent

object EmployeeUpdatedKafkaEvent {
  implicit val format: Format[EmployeeUpdatedKafkaEvent] = Json.format[EmployeeUpdatedKafkaEvent]
}
