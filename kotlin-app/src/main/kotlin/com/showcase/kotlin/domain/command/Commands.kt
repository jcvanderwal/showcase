package com.showcase.kotlin.domain.command

import com.showcase.kotlin.domain.model.Priority
import com.showcase.kotlin.domain.model.ProjectId
import com.showcase.kotlin.domain.model.TaskId
import com.showcase.kotlin.domain.model.UserId
import java.time.LocalDate

sealed class Command {
    abstract val aggregateId: String

    data class CreateTask(
        val taskId: TaskId,
        val title: String,
        val description: String?,
        val priority: Priority,
        val dueDate: LocalDate?,
        val tags: Set<String> = emptySet()
    ) : Command() {
        override val aggregateId: String get() = taskId.value
    }

    data class AssignTask(
        val taskId: TaskId,
        val assigneeId: UserId
    ) : Command() {
        override val aggregateId: String get() = taskId.value
    }

    data class ChangeTaskPriority(
        val taskId: TaskId,
        val newPriority: Priority
    ) : Command() {
        override val aggregateId: String get() = taskId.value
    }

    data class CompleteTask(
        val taskId: TaskId
    ) : Command() {
        override val aggregateId: String get() = taskId.value
    }

    data class AddTagToTask(
        val taskId: TaskId,
        val tag: String
    ) : Command() {
        override val aggregateId: String get() = taskId.value
    }

    data class MoveTaskToProject(
        val taskId: TaskId,
        val projectId: ProjectId
    ) : Command() {
        override val aggregateId: String get() = taskId.value
    }
}
