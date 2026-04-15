package com.showcase.kotlin.slice.sealedclasses

import com.showcase.kotlin.domain.command.Command
import com.showcase.kotlin.domain.event.DomainEvent
import com.showcase.kotlin.domain.model.Priority
import java.time.Instant

/**
 * Demonstrates Kotlin's exhaustive `when` expression for sealed class command dispatch.
 *
 * Key Kotlin features showcased:
 * - Exhaustive `when` as an expression (returns a value directly)
 * - Smart casts inside `when` branches — no explicit casting needed
 * - Concise data-carrying sealed class variants
 * - Compile-time guarantee: adding a new Command subclass forces handling here
 */
class KotlinCommandHandler {

    /**
     * Dispatches a [Command] to produce [DomainEvent]s using an exhaustive `when` expression.
     *
     * The `when` expression is exhaustive because [Command] is a sealed class — the compiler
     * verifies every variant is handled. No `else` branch is needed or desired, because an
     * `else` would silently swallow new variants added in the future.
     */
    fun handle(command: Command): List<DomainEvent> = when (command) {
        // Smart cast: `command` is automatically narrowed to Command.CreateTask here.
        // All properties (taskId, title, description, priority, dueDate, tags) are
        // directly accessible without any cast.
        is Command.CreateTask -> {
            val now = Instant.now()
            listOf(
                DomainEvent.TaskCreated(
                    aggregateId = command.taskId.value,
                    timestamp = now,
                    title = command.title,
                    description = command.description,
                    priority = command.priority,
                    dueDate = command.dueDate,
                    tags = command.tags
                )
            )
        }

        // Smart cast to Command.AssignTask — assigneeId is directly accessible
        is Command.AssignTask -> listOf(
            DomainEvent.TaskAssigned(
                aggregateId = command.taskId.value,
                timestamp = Instant.now(),
                assigneeId = command.assigneeId
            )
        )

        // Smart cast to Command.ChangeTaskPriority — newPriority is directly accessible
        is Command.ChangeTaskPriority -> listOf(
            DomainEvent.TaskPriorityChanged(
                aggregateId = command.taskId.value,
                timestamp = Instant.now(),
                oldPriority = Priority.MEDIUM, // simplified — real impl loads current
                newPriority = command.newPriority
            )
        )

        // Smart cast to Command.CompleteTask
        is Command.CompleteTask -> {
            val now = Instant.now()
            listOf(
                DomainEvent.TaskCompleted(
                    aggregateId = command.taskId.value,
                    timestamp = now,
                    completedAt = now
                )
            )
        }

        // Smart cast to Command.AddTagToTask — tag is directly accessible
        is Command.AddTagToTask -> listOf(
            DomainEvent.TagAddedToTask(
                aggregateId = command.taskId.value,
                timestamp = Instant.now(),
                tag = command.tag
            )
        )

        // Smart cast to Command.MoveTaskToProject — projectId is directly accessible
        is Command.MoveTaskToProject -> listOf(
            DomainEvent.TaskMovedToProject(
                aggregateId = command.taskId.value,
                timestamp = Instant.now(),
                projectId = command.projectId
            )
        )

        // No `else` branch — the compiler enforces exhaustiveness.
        // If a new Command variant is added, this file will not compile until it is handled.
    }
}
