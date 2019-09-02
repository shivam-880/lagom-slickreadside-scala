package com.codingkapoor.employee.persistence.read

import akka.Done
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

  def getEmployees: Future[Seq[EmployeeEntity]] = {
    db.run(employees.result)
  }

  def getEmployee(id: String): Future[Option[EmployeeEntity]] = {
    db.run(employees.filter(_.id === id).result.headOption)
  }

  def createTable: DBIO[Unit] = employees.schema.createIfNotExists
}
