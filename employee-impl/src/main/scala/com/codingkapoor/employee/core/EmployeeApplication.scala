package com.codingkapoor.employee.core

import com.codingkapoor.employee.api.EmployeeService
import com.codingkapoor.employee.persistence.read.{EmployeeEventProcessor, EmployeeRepository}
import com.codingkapoor.employee.persistence.write.{EmployeePersistenceEntity, EmployeeSerializerRegistry}
import com.codingkapoor.employee.service.EmployeeServiceImpl
import com.lightbend.lagom.scaladsl.persistence.cassandra.WriteSideCassandraPersistenceComponents
import com.lightbend.lagom.scaladsl.server.{LagomApplication, LagomApplicationContext}
import com.lightbend.lagom.scaladsl.persistence.slick.ReadSideSlickPersistenceComponents
import com.softwaremill.macwire._
import play.api.db.HikariCPComponents
import play.api.libs.ws.ahc.AhcWSComponents

abstract class EmployeeApplication(context: LagomApplicationContext)
  extends LagomApplication(context)
    with ReadSideSlickPersistenceComponents
    with WriteSideCassandraPersistenceComponents
    with HikariCPComponents
    with AhcWSComponents {

  override lazy val lagomServer = serverFor[EmployeeService](wire[EmployeeServiceImpl])
  override lazy val jsonSerializerRegistry = EmployeeSerializerRegistry

  lazy val employeeRepository = wire[EmployeeRepository]

  persistentEntityRegistry.register(wire[EmployeePersistenceEntity])
  readSide.register(wire[EmployeeEventProcessor])
}
