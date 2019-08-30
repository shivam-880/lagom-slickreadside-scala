package com.codingkapoor.employee.persistence.write

import akka.Done
import com.lightbend.lagom.scaladsl.persistence.PersistentEntity

class EmployeePersistenceEntity extends PersistentEntity {
  override type Command = EmployeeCommand[_]
  override type Event = EmployeeEvent
  override type State = Option[EmployeeState]

  override def initialState: Option[EmployeeState] = None

  override def behavior: Behavior =
    Actions().onCommand[AddEmployee, Done] {
      case (AddEmployee(e), ctx, _) =>
        ctx.thenPersist(EmployeeAdded(e.id, e.name, e.gender, e.doj, e.pfn)) { _ =>
          ctx.reply(Done)
        }
    }.onCommand[UpdateEmployee, Done] {
      case (UpdateEmployee(e), ctx, _) =>
        ctx.thenPersist(EmployeeUpdated(e.id, e.name, e.gender, e.doj, e.pfn)) { _ =>
          ctx.reply(Done)
        }
    }.onEvent {
      case (EmployeeAdded(id, name, gender, doj, pfn), _) =>
        Some(EmployeeState(id, name, gender, doj, pfn))
    }.onEvent {
      case (EmployeeUpdated(id, name, gender, doj, pfn), _) =>
        Some(EmployeeState(id, name, gender, doj, pfn))
    }
}
