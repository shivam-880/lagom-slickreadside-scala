package com.codingkapoor.employee.persistence.read

import akka.Done
import com.codingkapoor.employee.persistence.write.{EmployeeAdded, EmployeeEvent, EmployeeUpdated}
import com.lightbend.lagom.scaladsl.persistence.{AggregateEventTag, EventStreamElement, ReadSideProcessor}
import com.lightbend.lagom.scaladsl.persistence.slick.SlickReadSide
import org.slf4j.LoggerFactory
import slick.jdbc.MySQLProfile.api._

class EmployeeEventProcessor(readSide: SlickReadSide, employeeRepository: EmployeeRepository)
  extends ReadSideProcessor[EmployeeEvent] {

  private val log = LoggerFactory.getLogger(classOf[EmployeeEventProcessor])

  override def buildHandler(): ReadSideProcessor.ReadSideHandler[EmployeeEvent] =
    readSide
      .builder[EmployeeEvent]("employeeoffset")
      .setGlobalPrepare(employeeRepository.createTable)
      .setEventHandler[EmployeeAdded](processEmployeeAdded)
      .setEventHandler[EmployeeUpdated](processEmployeeUpdated)
      .build()

  override def aggregateTags: Set[AggregateEventTag[EmployeeEvent]] = Set(EmployeeEvent.Tag)

  private def processEmployeeAdded(eventElement: EventStreamElement[EmployeeAdded]): DBIO[Done] = {
    log.info(s"EmployeeEventProcessor received EmployeeAdded event.")

    val employeeAdded = eventElement.event
    val employee = EmployeeEntity(employeeAdded.id, employeeAdded.name, employeeAdded.gender, employeeAdded.doj, employeeAdded.pfn)

    employeeRepository.addEmployee(employee)
  }

  private def processEmployeeUpdated(eventElement: EventStreamElement[EmployeeUpdated]): DBIO[Done] = {
    log.info(s"EmployeeEventProcessor received EmployeeUpdated event.")

    val employeeUpdated = eventElement.event
    val employee = EmployeeEntity(employeeUpdated.id, employeeUpdated.name, employeeUpdated.gender, employeeUpdated.doj, employeeUpdated.pfn)

    employeeRepository.updateEmployee(employee)
  }

}
