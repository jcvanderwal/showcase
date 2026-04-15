package com.showcase.kotlin.domain.projection

import com.showcase.kotlin.domain.event.DomainEvent
import com.showcase.kotlin.domain.model.TaskStatus
import com.showcase.kotlin.domain.model.TaskSummary
import java.time.LocalDate
import java.util.concurrent.ConcurrentHashMap

class OverdueTasksProjection : Projection {
    private val tasks = ConcurrentHashMap<String, TaskSummary>()

    override suspend fun handle(event: DomainEvent) {
        when (event) {
            is DomainEvent.TaskCreated -> tasks[event.aggregateId] = TaskSummary(
                id = event.aggregateId,
                title = event.title,
                status = TaskStatus.Open,
                priority = event.priority,
                assignee = null,
                dueDate = event.dueDate,
                tags = event.tags
            )
            is DomainEvent.TaskAssigned -> tasks.computeIfPresent(event.aggregateId) { _, summary ->
                summary.copy(
                    assignee = event.assigneeId,
                    status = TaskStatus.InProgress(event.assigneeId)
                )
            }
            is DomainEvent.TaskPriorityChanged -> tasks.computeIfPresent(event.aggregateId) { _, summary ->
                summary.copy(priority = event.newPriority)
            }
            is DomainEvent.TaskCompleted -> tasks.computeIfPresent(event.aggregateId) { _, summary ->
                summary.copy(status = TaskStatus.Completed(event.completedAt))
            }
            is DomainEvent.TagAddedToTask -> tasks.computeIfPresent(event.aggregateId) { _, summary ->
                summary.copy(tags = summary.tags + event.tag)
            }
            is DomainEvent.TaskMovedToProject -> {
                // Project move doesn't affect overdue tracking
            }
        }
    }

    fun getOverdueTasks(asOf: LocalDate = LocalDate.now()): List<TaskSummary> =
        tasks.values.filter { summary ->
            summary.dueDate != null &&
                summary.dueDate.isBefore(asOf) &&
                summary.status !is TaskStatus.Completed
        }
}
