package com.showcase.kotlin.domain.projection

import com.showcase.kotlin.domain.event.DomainEvent
import com.showcase.kotlin.domain.model.Priority
import com.showcase.kotlin.domain.model.TaskStatus
import com.showcase.kotlin.domain.model.TaskSummary
import com.showcase.kotlin.domain.model.UserId
import java.util.concurrent.ConcurrentHashMap

class TaskSummaryProjection : Projection {
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
                // Project move doesn't change task summary fields directly
            }
        }
    }

    fun getAll(): List<TaskSummary> = tasks.values.toList()

    fun getById(id: String): TaskSummary? = tasks[id]

    fun getByStatus(status: TaskStatus): List<TaskSummary> =
        tasks.values.filter { it.status == status }
}
