package com.showcase.kotlin.slice.extensions

import com.showcase.kotlin.domain.event.DomainEvent
import java.time.Instant

/**
 * Slice 4 — Extension Functions (Req 5)
 *
 * Extension functions and properties on List<DomainEvent>.
 * These read like natural operations on event streams — e.g. events.filterByTaskId("task-1")
 * — which is impossible to achieve with Java static utility methods.
 */

// --- Extension Functions ---

/**
 * Filters domain events to only those belonging to the given aggregate (task) ID.
 */
fun List<DomainEvent>.filterByTaskId(taskId: String): List<DomainEvent> =
    filter { it.aggregateId == taskId }

// --- Extension Properties ---

/**
 * Returns the most recent timestamp across all events in this list, or null if empty.
 */
val List<DomainEvent>.latestTimestamp: Instant?
    get() = maxOfOrNull { it.timestamp }
