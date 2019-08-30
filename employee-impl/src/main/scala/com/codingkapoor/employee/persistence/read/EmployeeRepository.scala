package com.codingkapoor.employee.persistence.read

import akka.Done
import com.codingkapoor.employee.api.Employee
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class EmployeeRepository(db: Database) {

  val employees = EmployeeTableDef.employees

  def addEmployee(employee: EmployeeEntity): DBIO[Done] = {
    (employees += employee).map(_ => Done)
  }

  def updateEmployee(employee: EmployeeEntity): DBIO[Done] = {
    employees.insertOrUpdate(employee).map(_ => Done)
  }

  def getEmployees: Future[Seq[Employee]] = {
    db.run(employees.result).map(_.map(convertEmployeeReadEntityToEmployee))
  }

  def getEmployee(id: String): Future[Employee] = {
    db.run(employees.filter(_.id === id).result.head).map(convertEmployeeReadEntityToEmployee)
  }

  private def convertEmployeeReadEntityToEmployee(e: EmployeeEntity): Employee = {
    Employee(e.id, e.name, e.gender, e.doj, e.pfn)
  }

  def createTable: DBIO[Unit] = employees.schema.createIfNotExists
}
