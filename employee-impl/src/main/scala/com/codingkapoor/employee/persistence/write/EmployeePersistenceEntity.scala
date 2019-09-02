package com.codingkapoor.employee.persistence.write

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity
import org.slf4j.LoggerFactory

class EmployeePersistenceEntity extends PersistentEntity {

  private val log = LoggerFactory.getLogger(classOf[EmployeePersistenceEntity])

  override type Command = EmployeeCommand[_]
  override type Event = EmployeeEvent
  override type State = Option[EmployeeState]

  override def initialState: Option[EmployeeState] = None

  override def behavior: Behavior = {
    case state if state.isEmpty => initial
    case state if state.isDefined => employeeAdded
  }

  private val initial: Actions =
    Actions().onCommand[AddEmployee, Done] {
      case (AddEmployee(e), ctx, state) =>
        log.info(s"EmployeePersistenceEntity at state = $state received AddEmployee command.")

        ctx.thenPersist(EmployeeAdded(e.id, e.name, e.gender, e.doj, e.pfn)) { _ =>
          ctx.reply(Done)
        }
    }.onCommand[UpdateEmployee, Done]{
      case(UpdateEmployee(e), ctx, state) =>
        log.info(s"EmployeePersistenceEntity at state = $state received UpdateEmployee command.")

        val msg = s"No employee found with id = ${e.id}."
        ctx.invalidCommand(msg)

        log.info(s"InvalidCommandException: $msg")

        ctx.done
    }.onEvent {
      case (EmployeeAdded(id, name, gender, doj, pfn), _) =>
        Some(EmployeeState(id, name, gender, doj, pfn))
    }

  private val employeeAdded: Actions =
    Actions().onCommand[AddEmployee, Done]{
      case (AddEmployee(e), ctx, state) =>
        log.info(s"EmployeePersistenceEntity at state = $state received AddEmployee command.")

        val msg = s"Employee with id = ${e.id} already exists."
        ctx.invalidCommand(msg)

        log.info(s"InvalidCommandException: $msg")

        ctx.done
    }.onCommand[UpdateEmployee, Done] {
      case (UpdateEmployee(e), ctx, state) =>
        log.info(s"EmployeePersistenceEntity at state = $state received UpdateEmployee command.")

        ctx.thenPersist(EmployeeUpdated(e.id, e.name, e.gender, e.doj, e.pfn)) { _ =>
          ctx.reply(Done)
        }
    }.onEvent {
      case (EmployeeUpdated(id, name, gender, doj, pfn), _) =>
        Some(EmployeeState(id, name, gender, doj, pfn))
    }
}
