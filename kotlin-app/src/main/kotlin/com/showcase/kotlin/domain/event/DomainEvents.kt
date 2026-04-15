package com.showcase.kotlin.domain.event

import com.showcase.kotlin.domain.model.Priority
import com.showcase.kotlin.domain.model.ProjectId
import com.showcase.kotlin.domain.model.UserId
import java.time.Instant
import java.time.LocalDate

sealed class DomainEvent {
    abstract val timestamp: Instant
    abstract val aggregateId: String

    data class TaskCreated(
        override val aggregateId: String,
        override val timestamp: Instant,
        val title: String,
        val description: String?,
        val priority: Priority,
        val dueDate: LocalDate?,
        val tags: Set<String>
    ) : DomainEvent()

    data class TaskAssigned(
        override val aggregateId: String,
        override val timestamp: Instant,
        val assigneeId: UserId
    ) : DomainEvent()

    data class TaskPriorityChanged(
        override val aggregateId: String,
        override val timestamp: Instant,
        val oldPriority: Priority,
        val newPriority: Priority
    ) : DomainEvent()

    data class TaskCompleted(
        override val aggregateId: String,
        override val timestamp: Instant,
        val completedAt: Instant
    ) : DomainEvent()

    data class TagAddedToTask(
        override val aggregateId: String,
        override val timestamp: Instant,
        val tag: String
    ) : DomainEvent()

    data class TaskMovedToProject(
        override val aggregateId: String,
        override val timestamp: Instant,
        val projectId: ProjectId
    ) : DomainEvent()
}
