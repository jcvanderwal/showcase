package com.showcase.kotlin.slice.extensions

import com.showcase.kotlin.domain.model.TaskStatus
import com.showcase.kotlin.domain.model.TaskSummary
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Slice 4 — Extension Functions (Req 5)
 *
 * Demonstrates Kotlin extension functions and extension properties on domain types.
 * These add behavior to TaskSummary without modifying the original data class,
 * improving discoverability via IDE auto-complete — something Java 25 cannot replicate.
 */

// --- Extension Functions ---

/**
 * Returns true if this task is overdue: it has a due date in the past
 * and is not yet completed.
 */
fun TaskSummary.isOverdue(): Boolean {
    val due = dueDate ?: return false
    return due.isBefore(LocalDate.now()) && status !is TaskStatus.Completed
}

/**
 * Groups a list of task summaries by their current status class.
 */
fun List<TaskSummary>.groupByStatus(): Map<String, List<TaskSummary>> =
    groupBy { it.status::class.simpleName ?: "Unknown" }

/**
 * Converts a string to a URL-friendly slug.
 * Useful for generating task or project slugs from titles.
 */
fun String.toSlug(): String =
    this.trim()
        .lowercase()
        .replace(Regex("[^a-z0-9\\s-]"), "")
        .replace(Regex("\\s+"), "-")
        .replace(Regex("-+"), "-")
        .trimEnd('-')

// --- Extension Properties ---

/**
 * Returns the number of days until the due date, or null if no due date is set.
 * Negative values indicate the task is past due.
 */
val TaskSummary.daysUntilDue: Long?
    get() = dueDate?.let { ChronoUnit.DAYS.between(LocalDate.now(), it) }
