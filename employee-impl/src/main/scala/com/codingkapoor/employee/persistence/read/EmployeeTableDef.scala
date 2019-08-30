package com.codingkapoor.employee.persistence.read

import java.time.LocalDate
import slick.jdbc.MySQLProfile.api._

case class EmployeeEntity(id: String, name: String, gender: String, doj: LocalDate, pfn: String)

class EmployeeTableDef(tag: Tag) extends Table[EmployeeEntity](tag, "employee") {

  def id = column[String]("ID", O.PrimaryKey)

  def name = column[String]("NAME")

  def gender = column[String]("GENDER")

  def doj = column[LocalDate]("DOJ")

  def pfn = column[String]("PFN")

  override def * =
    (id, name, gender, doj, pfn) <> (EmployeeEntity.tupled, EmployeeEntity.unapply)
}

object EmployeeTableDef {
  val employees = TableQuery[EmployeeTableDef]
}
