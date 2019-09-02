package com.codingkapoor.employee.service

import akka.{Done, NotUsed}
import com.codingkapoor.employee.api
import com.codingkapoor.employee.api.{Employee, EmployeeService}
import com.codingkapoor.employee.persistence.read.{EmployeeEntity, EmployeeRepository}
import com.codingkapoor.employee.persistence.write._
import com.lightbend.lagom.scaladsl.api.ServiceCall
import com.lightbend.lagom.scaladsl.api.broker.Topic
import com.lightbend.lagom.scaladsl.broker.TopicProducer
import com.lightbend.lagom.scaladsl.persistence.{EventStreamElement, PersistentEntityRegistry}
import com.lightbend.lagom.scaladsl.api.transport.{BadRequest, NotFound}
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity.InvalidCommandException

import scala.concurrent.ExecutionContext.Implicits.global

class EmployeeServiceImpl(persistentEntityRegistry: PersistentEntityRegistry, employeeRepository: EmployeeRepository) extends EmployeeService {

  import EmployeeServiceImpl._

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
      else throw NotFound(s"No employee found with id = $id")
    }
  }

  override def employeeTopic: Topic[api.EmployeeKafkaEvent] = {
    TopicProducer.singleStreamWithOffset { fromOffset =>
      persistentEntityRegistry.eventStream(EmployeeEvent.Tag, fromOffset)
        .map(event => (convertPersistentEntityEventToKafkaEvent(event), event.offset))
    }
  }

}

object EmployeeServiceImpl {

  private def convertPersistentEntityEventToKafkaEvent(eventStreamElement: EventStreamElement[EmployeeEvent]): api.EmployeeKafkaEvent = {
    eventStreamElement.event match {
      case EmployeeAdded(id, name, gender, doj, pfn) => api.EmployeeAddedKafkaEvent(id, name, gender, doj, pfn)
      case EmployeeUpdated(id, name, gender, doj, pfn) => api.EmployeeUpdatedKafkaEvent(id, name, gender, doj, pfn)
    }
  }

  private def convertEmployeeReadEntityToEmployee(e: EmployeeEntity): api.Employee = {
    api.Employee(e.id, e.name, e.gender, e.doj, e.pfn)
  }
}
