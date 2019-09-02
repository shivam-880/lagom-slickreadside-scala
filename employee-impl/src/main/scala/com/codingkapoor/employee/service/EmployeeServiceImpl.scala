package com.codingkapoor.employee.service

import akka.{Done, NotUsed}
import com.codingkapoor.employee.api
import com.codingkapoor.employee.api.{Employee, EmployeeService}
import com.codingkapoor.employee.persistence.read.{EmployeeEntity, EmployeeRepository}
import com.codingkapoor.employee.persistence.write._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.persistence.PersistentEntityRegistry
import com.lightbend.lagom.scaladsl.api.transport.{BadRequest, NotFound}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.InvalidCommandException
import org.slf4j.LoggerFactory

import scala.concurrent.ExecutionContext.Implicits.global

class EmployeeServiceImpl(persistentEntityRegistry: PersistentEntityRegistry, employeeRepository: EmployeeRepository) extends EmployeeService {

  import EmployeeServiceImpl._

  private val log = LoggerFactory.getLogger(classOf[EmployeeServiceImpl])

  private def entityRef(id: String) = persistentEntityRegistry.refFor[EmployeePersistenceEntity](id)

  override def addEmployee(): ServiceCall[Employee, Done] = ServiceCall { employee =>
    entityRef(employee.id).ask(AddEmployee(employee)).recover {
        case e: InvalidCommandException => throw BadRequest(e.getMessage)
      }
  }

  override def updateEmployee(id: String): ServiceCall[Employee, Done] = { employee =>
    entityRef(employee.id).ask(UpdateEmployee(employee)).recover {
        case e: InvalidCommandException => throw BadRequest(e.getMessage)
      }
  }

  override def getEmployees: ServiceCall[NotUsed, Seq[Employee]] = ServiceCall { _ =>
    employeeRepository.getEmployees.map(_.map(convertEmployeeReadEntityToEmployee))
  }

  override def getEmployee(id: String): ServiceCall[NotUsed, Employee] = ServiceCall { _ =>
    employeeRepository.getEmployee(id).map { e =>
      if (e.isDefined) convertEmployeeReadEntityToEmployee(e.get)
      else {
        val msg = s"No employee found with id = $id."
        log.error(msg)

        throw NotFound(msg)
      }
    }
  }
}

object EmployeeServiceImpl {

  private def convertEmployeeReadEntityToEmployee(e: EmployeeEntity): api.Employee = {
    api.Employee(e.id, e.name, e.gender, e.doj, e.pfn)
  }
}
