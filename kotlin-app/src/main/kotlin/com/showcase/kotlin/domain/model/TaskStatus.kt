package com.showcase.kotlin.domain.model

import java.time.Instant

sealed class TaskStatus {
    data object Open : TaskStatus()
    data class InProgress(val assignee: UserId) : TaskStatus()
    data class Blocked(val reason: String) : TaskStatus()
    data class Completed(val completedAt: Instant) : TaskStatus()
}
