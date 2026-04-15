package com.showcase.kotlin.slice.scopefunctions

import com.showcase.kotlin.domain.command.Command
import com.showcase.kotlin.domain.command.CommandHandler
import com.showcase.kotlin.domain.event.DomainEvent
import com.showcase.kotlin.domain.model.*
import com.showcase.kotlin.domain.store.EventStore
import java.time.Instant
import java.time.LocalDate

/**
 * Slice 8 — Scope Functions: ScopeFunctionShowcase
 *
 * Demonstrates Kotlin's scope functions (let, run, with, apply, also),
 * data class copy(), destructuring declarations, and string templates
 * in realistic event sourcing scenarios.
 *
 * Java 25 has narrowed the gap with records, record patterns, and var,
 * but Kotlin's scope functions and copy() remain more concise.
 */
class ScopeFunctionShowcase(
    private val eventStore: EventStore,
    private val handler: CommandHandler
) {

    // ── let: Transform nullable event fields ──────────────────────────
    /**
     * Uses `let` to safely transform a nullable assignee from a TaskAssigned event.
     * If the event has an assigneeId, maps it to a display string; otherwise returns a default.
     */
    fun formatAssignment(event: DomainEvent.TaskAssigned?): String =
        event?.assigneeId?.let { userId ->
            "Task ${event.aggregateId} assigned to ${userId.value}"
        } ?: "No assignment"

    // ── run: Configure and execute in one block ───────────────────────
    /**
     * Uses `run` to configure a command handler and immediately execute a command.
     * `run` is ideal when you need to call methods on an object and return a result.
     */
    suspend fun createAndReturnEvents(
        taskId: TaskId,
        title: String,
        priority: Priority
    ): List<DomainEvent> = handler.run {
        handle(
            Command.CreateTask(
                taskId = taskId,
                title = title,
                description = null,
                priority = priority,
                dueDate = null,
                tags = emptySet()
            )
        )
    }

    // ── with: Build projection summaries ──────────────────────────────
    /**
     * Uses `with` to work with a TaskSummary without repeating the receiver.
     * Ideal for building formatted output from an object's properties.
     */
    fun summarize(task: TaskSummary): String = with(task) {
        """
        |Task: $title (${id})
        |Status: $status
        |Priority: $priority
        |Tags: ${tags.joinToString(", ")}
        |Due: ${dueDate ?: "No due date"}
        """.trimMargin()
    }

    // ── apply: Initialize aggregate state ─────────────────────────────
    /**
     * Uses `apply` to build a TaskSummary with fluent initialization.
     * `apply` returns the receiver itself, making it ideal for object configuration.
     */
    fun buildDefaultTask(id: String, title: String): TaskSummary =
        TaskSummary(
            id = id,
            title = title,
            status = TaskStatus.Open,
            priority = Priority.LOW,
            assignee = null,
            dueDate = null,
            tags = emptySet()
        ).let { task ->
            // apply-style: we use copy to set defaults, returning the modified task
            task.copy(tags = setOf("new"))
        }

    // ── also: Logging side effects during event processing ────────────
    /**
     * Uses `also` for side effects (logging) without altering the return value.
     * The event list flows through unchanged while logging happens as a side effect.
     */
    fun processEventsWithLogging(events: List<DomainEvent>): List<DomainEvent> =
        events.also { eventList ->
            eventList.forEach { event ->
                println("[LOG] Processing event: ${event::class.simpleName} for aggregate ${event.aggregateId}")
            }
        }

    // ── data class copy(): Create modified Domain Events ──────────────
    /**
     * Demonstrates `copy()` for creating modified immutable events.
     * Java records have no built-in copy() — you must manually construct a new record.
     */
    fun reassignEvent(
        original: DomainEvent.TaskAssigned,
        newAssignee: UserId
    ): DomainEvent.TaskAssigned =
        original.copy(assigneeId = newAssignee, timestamp = Instant.now())

    fun reprioritizeEvent(
        original: DomainEvent.TaskPriorityChanged,
        newPriority: Priority
    ): DomainEvent.TaskPriorityChanged =
        original.copy(newPriority = newPriority, timestamp = Instant.now())

    // ── Destructuring declarations: Pattern matching on events ────────
    /**
     * Demonstrates destructuring declarations for extracting event fields.
     * Kotlin data classes automatically generate componentN() functions.
     */
    fun describeCreation(event: DomainEvent.TaskCreated): String {
        val (aggregateId, timestamp, title, description, priority, dueDate, tags) = event
        return "Created '$title' (id=$aggregateId) at $timestamp with priority $priority" +
                (description?.let { ", desc: $it" } ?: "") +
                (dueDate?.let { ", due: $it" } ?: "") +
                if (tags.isNotEmpty()) ", tags: ${tags.joinToString()}" else ""
    }

    // ── String templates: Event logging ───────────────────────────────
    /**
     * Demonstrates string templates for concise event logging.
     * Java requires String.format() or concatenation for the same result.
     */
    fun logEvent(event: DomainEvent): String = when (event) {
        is DomainEvent.TaskCreated ->
            "📋 Task '${event.title}' created with priority ${event.priority} at ${event.timestamp}"
        is DomainEvent.TaskAssigned ->
            "👤 Task ${event.aggregateId} assigned to ${event.assigneeId.value} at ${event.timestamp}"
        is DomainEvent.TaskPriorityChanged ->
            "⚡ Task ${event.aggregateId} priority changed: ${event.oldPriority} → ${event.newPriority}"
        is DomainEvent.TaskCompleted ->
            "✅ Task ${event.aggregateId} completed at ${event.completedAt}"
        is DomainEvent.TagAddedToTask ->
            "🏷️ Tag '${event.tag}' added to task ${event.aggregateId}"
        is DomainEvent.TaskMovedToProject ->
            "📁 Task ${event.aggregateId} moved to project ${event.projectId.value}"
    }
}
