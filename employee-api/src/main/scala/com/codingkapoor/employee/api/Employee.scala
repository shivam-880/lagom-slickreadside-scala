package com.codingkapoor.employee.api

import java.time.LocalDate

import play.api.libs.json.{Format, Json}

case class Employee(id: String, name: String, gender: String, doj: LocalDate, pfn: String)

object Employee {
  implicit val format: Format[Employee] = Json.format[Employee]
}
