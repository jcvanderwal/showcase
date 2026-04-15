package com.showcase.kotlin.domain.model

import java.time.LocalDate

data class TaskSummary(
    val id: String,
    val title: String,
    val status: TaskStatus,
    val priority: Priority,
    val assignee: UserId?,
    val dueDate: LocalDate?,
    val tags: Set<String>
)
