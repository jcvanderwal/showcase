package com.showcase.kotlin.slice.nullsafety

import com.showcase.kotlin.domain.event.DomainEvent
import com.showcase.kotlin.domain.model.*
import java.time.Instant
import java.time.LocalDate

/**
 * Demonstrates Kotlin's compile-time null safety in an event-sourced task workflow.
 *
 * Key Kotlin features showcased:
 * - Nullable vs non-nullable types enforced at compile time
 * - Safe call operator `?.`
 * - Elvis operator `?:`
 * - Scope functions `let` and `also` for null handling
 * - Compile-time rejection of unsafe null operations (shown in comments)
 *
 * Parity Level: "Kotlin still leads" — Kotlin enforces null safety at compile time,
 * while Java 25 relies on runtime checks, Optional, and annotations.
 */
class NullSafeWorkflow(
    private val tasks: MutableMap<String, TaskSummary> = mutableMapOf()
) {

    // -----------------------------------------------------------------------
    // COMPILE-TIME REJECTION EXAMPLES
    // The following lines would NOT compile in Kotlin. They are commented out
    // to show what the compiler rejects.
    // -----------------------------------------------------------------------

    // ERROR: Null can not be a value of a non-null type String
    // val title: String = null

    // ERROR: Only safe (?.) or non-null asserted (!!) calls are allowed
    //        on a nullable receiver of type String?
    // val len: Int = nullableString.length

    // ERROR: Type mismatch — required: UserId, found: UserId?
    // fun assignTask(assignee: UserId?) { val id: UserId = assignee }

    // -----------------------------------------------------------------------
    // SAFE CALL CHAINS (?.)
    // -----------------------------------------------------------------------

    /**
     * Looks up a task and returns its assignee name using safe call chains.
     *
     * `tasks[taskId]` returns `TaskSummary?` (nullable).
     * `?.assignee` safely accesses the nullable assignee field.
     * `?.value` safely accesses the UserId value.
     * If any link in the chain is null, the entire expression evaluates to null.
     */
    fun findAssigneeName(taskId: String): String? =
        tasks[taskId]?.assignee?.value

    /**
     * Returns the due date of a task, or null if the task doesn't exist or has no due date.
     * Demonstrates chained safe calls on nullable properties.
     */
    fun findDueDate(taskId: String): LocalDate? =
        tasks[taskId]?.dueDate

    // -----------------------------------------------------------------------
    // ELVIS OPERATOR (?:)
    // -----------------------------------------------------------------------

    /**
     * Returns the assignee name or a default placeholder.
     *
     * The elvis operator `?:` provides a fallback when the left side is null.
     * This replaces verbose if-null-then-else patterns.
     */
    fun getAssigneeOrDefault(taskId: String): String =
        tasks[taskId]?.assignee?.value ?: "unassigned"

    /**
     * Returns the task title or throws if the task doesn't exist.
     *
     * Elvis can also be used with `throw` for fail-fast behavior.
     */
    fun getTaskTitleOrThrow(taskId: String): String =
        tasks[taskId]?.title ?: throw NoSuchElementException("Task $taskId not found")

    // -----------------------------------------------------------------------
    // SCOPE FUNCTIONS FOR NULL HANDLING (let, also)
    // -----------------------------------------------------------------------

    /**
     * Processes a task assignment using `let` for null-safe transformation.
     *
     * `?.let { ... }` executes the block only if the receiver is non-null.
     * Inside the lambda, `it` is guaranteed non-null — no further null checks needed.
     */
    fun processAssignment(taskId: String, assigneeId: UserId): TaskSummary? =
        tasks[taskId]?.let { task ->
            // `task` is guaranteed non-null here — smart cast by `let`
            val updated = task.copy(
                assignee = assigneeId,
                status = TaskStatus.InProgress(assignee = assigneeId)
            )
            tasks[taskId] = updated
            updated
        }

    /**
     * Looks up a task and logs it using `also` for side effects.
     *
     * `?.also { ... }` executes the side-effect block only if non-null,
     * then returns the original value unchanged.
     */
    fun findTaskWithLogging(taskId: String, logger: (String) -> Unit): TaskSummary? =
        tasks[taskId]?.also { task ->
            logger("Found task: ${task.title} [${task.status}]")
        }

    // -----------------------------------------------------------------------
    // COMBINED NULL SAFETY IN EVENT REPLAY
    // -----------------------------------------------------------------------

    /**
     * Replays events to build task state, demonstrating combined null safety patterns.
     *
     * - Non-nullable parameters: `events` list and each `DomainEvent` are non-null by type
     * - Nullable fields: `description`, `dueDate` are nullable in TaskCreated
     * - Safe calls + elvis for lookups during replay
     * - `let` for conditional updates on nullable task lookups
     */
    fun replayEvents(events: List<DomainEvent>) {
        for (event in events) {
            when (event) {
                is DomainEvent.TaskCreated -> {
                    tasks[event.aggregateId] = TaskSummary(
                        id = event.aggregateId,
                        title = event.title,
                        status = TaskStatus.Open,
                        priority = event.priority,
                        assignee = null,       // nullable — no assignee yet
                        dueDate = event.dueDate, // nullable — may or may not have a due date
                        tags = event.tags
                    )
                }

                is DomainEvent.TaskAssigned -> {
                    // Safe call + let: only update if the task exists
                    tasks[event.aggregateId]?.let { existing ->
                        tasks[event.aggregateId] = existing.copy(
                            assignee = event.assigneeId,
                            status = TaskStatus.InProgress(assignee = event.assigneeId)
                        )
                    }
                }

                is DomainEvent.TaskCompleted -> {
                    tasks[event.aggregateId]?.let { existing ->
                        tasks[event.aggregateId] = existing.copy(
                            status = TaskStatus.Completed(completedAt = event.completedAt)
                        )
                    }
                }

                is DomainEvent.TaskPriorityChanged -> {
                    tasks[event.aggregateId]?.let { existing ->
                        tasks[event.aggregateId] = existing.copy(priority = event.newPriority)
                    }
                }

                is DomainEvent.TagAddedToTask -> {
                    tasks[event.aggregateId]?.let { existing ->
                        tasks[event.aggregateId] = existing.copy(tags = existing.tags + event.tag)
                    }
                }

                is DomainEvent.TaskMovedToProject -> {
                    // No status change — task stays in current state
                }
            }
        }
    }

    /**
     * Returns all tasks currently tracked.
     */
    fun getAllTasks(): List<TaskSummary> = tasks.values.toList()
}
