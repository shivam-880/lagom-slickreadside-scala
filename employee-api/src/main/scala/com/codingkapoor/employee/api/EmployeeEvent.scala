package com.codingkapoor.employee.api

import java.time.LocalDate

import julienrf.json.derived
import play.api.libs.json._

sealed trait EmployeeEvent {
  val id: String
}

object EmployeeEvent {
  implicit val format: Format[EmployeeEvent] = derived.flat.oformat((__ \ "type").format[String])
}

case class EmployeeAdded(id: String, name: String, gender: String, doj: LocalDate, pfn: String) extends EmployeeEvent

object EmployeeAdded {
  implicit val format: Format[EmployeeAdded] = Json.format[EmployeeAdded]
}

case class EmployeeUpdated(id: String, name: String, gender: String, doj: LocalDate, pfn: String) extends EmployeeEvent

object EmployeeUpdated {
  implicit val format: Format[EmployeeUpdated] = Json.format[EmployeeUpdated]
}
