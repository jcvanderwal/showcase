package com.showcase.kotlin.slice.sealedclasses

import com.showcase.kotlin.domain.event.DomainEvent
import com.showcase.kotlin.domain.model.TaskStatus
import com.showcase.kotlin.domain.model.UserId

/**
 * Demonstrates Kotlin's exhaustive `when` expression for applying domain events
 * to produce task status transitions.
 *
 * Key Kotlin features showcased:
 * - Exhaustive `when` over sealed class [DomainEvent]
 * - Exhaustive `when` over sealed class [TaskStatus]
 * - Expression-based `when` returning values directly
 * - Smart casts within branches — no explicit casting
 * - Compile-time safety: new event or status variants force updates here
 */
class KotlinEventApplier {

    /**
     * Applies a [DomainEvent] to the current [TaskStatus] and returns the new status.
     *
     * Uses an exhaustive `when` over the sealed [DomainEvent] hierarchy.
     * Each branch smart-casts the event, giving direct access to its properties.
     */
    fun apply(event: DomainEvent, currentStatus: TaskStatus): TaskStatus = when (event) {
        // TaskCreated always produces Open status regardless of current state
        is DomainEvent.TaskCreated -> TaskStatus.Open

        // TaskAssigned transitions to InProgress with the assignee from the event.
        // Smart cast gives direct access to event.assigneeId.
        is DomainEvent.TaskAssigned -> TaskStatus.InProgress(assignee = event.assigneeId)

        // TaskCompleted transitions to Completed with the timestamp from the event.
        // Smart cast gives direct access to event.completedAt.
        is DomainEvent.TaskCompleted -> TaskStatus.Completed(completedAt = event.completedAt)

        // Priority changes, tag additions, and project moves don't affect task status.
        is DomainEvent.TaskPriorityChanged -> currentStatus
        is DomainEvent.TagAddedToTask -> currentStatus
        is DomainEvent.TaskMovedToProject -> currentStatus

        // No `else` — compiler enforces exhaustiveness over the sealed hierarchy.
    }

    /**
     * Demonstrates exhaustive `when` over the sealed [TaskStatus] hierarchy.
     *
     * Returns a human-readable description of the current status.
     * Each branch smart-casts to the specific status variant.
     */
    fun describeStatus(status: TaskStatus): String = when (status) {
        // data object — no properties to destructure
        is TaskStatus.Open -> "Task is open and awaiting assignment"

        // Smart cast: status.assignee is directly accessible
        is TaskStatus.InProgress -> "Task is in progress, assigned to ${status.assignee.value}"

        // Smart cast: status.reason is directly accessible
        is TaskStatus.Blocked -> "Task is blocked: ${status.reason}"

        // Smart cast: status.completedAt is directly accessible
        is TaskStatus.Completed -> "Task was completed at ${status.completedAt}"

        // No `else` — adding a new TaskStatus variant forces a compile error here.
    }

    /**
     * Replays a sequence of events to derive the final [TaskStatus].
     *
     * Demonstrates fold with exhaustive `when` — each event is applied in order.
     */
    fun replayEvents(events: List<DomainEvent>): TaskStatus =
        events.fold(TaskStatus.Open as TaskStatus) { status, event -> apply(event, status) }
}
