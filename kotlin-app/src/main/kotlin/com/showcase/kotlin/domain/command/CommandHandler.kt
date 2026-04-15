package com.showcase.kotlin.domain.command

import com.showcase.kotlin.domain.bus.EventBus
import com.showcase.kotlin.domain.event.DomainEvent
import com.showcase.kotlin.domain.store.EventStore
import java.time.Instant

class CommandHandler(
    private val eventStore: EventStore,
    private val eventBus: EventBus
) {
    suspend fun handle(command: Command): List<DomainEvent> {
        val events = when (command) {
            is Command.CreateTask -> handleCreate(command)
            is Command.AssignTask -> handleAssign(command)
            is Command.ChangeTaskPriority -> handleChangePriority(command)
            is Command.CompleteTask -> handleComplete(command)
            is Command.AddTagToTask -> handleAddTag(command)
            is Command.MoveTaskToProject -> handleMoveToProject(command)
        }
        eventStore.append(command.aggregateId, events)
        eventBus.publish(events)
        return events
    }

    private fun handleCreate(cmd: Command.CreateTask): List<DomainEvent> {
        val now = Instant.now()
        return listOf(
            DomainEvent.TaskCreated(
                aggregateId = cmd.taskId.value,
                timestamp = now,
                title = cmd.title,
                description = cmd.description,
                priority = cmd.priority,
                dueDate = cmd.dueDate,
                tags = cmd.tags
            )
        )
    }

    private fun handleAssign(cmd: Command.AssignTask): List<DomainEvent> =
        listOf(
            DomainEvent.TaskAssigned(
                aggregateId = cmd.taskId.value,
                timestamp = Instant.now(),
                assigneeId = cmd.assigneeId
            )
        )

    private fun handleChangePriority(cmd: Command.ChangeTaskPriority): List<DomainEvent> =
        listOf(
            DomainEvent.TaskPriorityChanged(
                aggregateId = cmd.taskId.value,
                timestamp = Instant.now(),
                oldPriority = cmd.newPriority, // simplified — real impl would load current priority
                newPriority = cmd.newPriority
            )
        )

    private fun handleComplete(cmd: Command.CompleteTask): List<DomainEvent> {
        val now = Instant.now()
        return listOf(
            DomainEvent.TaskCompleted(
                aggregateId = cmd.taskId.value,
                timestamp = now,
                completedAt = now
            )
        )
    }

    private fun handleAddTag(cmd: Command.AddTagToTask): List<DomainEvent> =
        listOf(
            DomainEvent.TagAddedToTask(
                aggregateId = cmd.taskId.value,
                timestamp = Instant.now(),
                tag = cmd.tag
            )
        )

    private fun handleMoveToProject(cmd: Command.MoveTaskToProject): List<DomainEvent> =
        listOf(
            DomainEvent.TaskMovedToProject(
                aggregateId = cmd.taskId.value,
                timestamp = Instant.now(),
                projectId = cmd.projectId
            )
        )
}
