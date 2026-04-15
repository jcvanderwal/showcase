package com.showcase.kotlin.slice.coroutines

import com.showcase.kotlin.domain.event.DomainEvent
import com.showcase.kotlin.domain.store.EventStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

/**
 * Demonstrates Kotlin Flow-based reactive streaming for a real-time task activity feed.
 *
 * Key Kotlin features showcased:
 * - `Flow` for cold, lazy, asynchronous streams
 * - Flow operators: `map`, `filter`, `onEach`
 * - `flow { }` builder for producing events
 * - Backpressure handled naturally by Flow's suspension model
 *
 * Requirements: 4.3
 *
 * Java 25 has no direct equivalent to Flow. Virtual threads handle concurrency
 * but don't provide a reactive streaming abstraction with built-in backpressure,
 * operators, and cold-stream semantics.
 */
class FlowActivityFeed(
    private val eventStore: EventStore
) {

    /**
     * Returns a Flow that replays all domain events from the event store.
     *
     * The Flow is cold — events are only loaded when a collector subscribes.
     * Each collector gets its own independent replay.
     */
    fun replayAllEvents(): Flow<DomainEvent> = flow {
        val events = eventStore.loadAll()
        for (event in events) {
            emit(event)
        }
    }

    /**
     * Returns a Flow of events for a specific aggregate, formatted as activity descriptions.
     *
     * Demonstrates Flow operators:
     * - `map` to transform events into human-readable strings
     * - Suspend-friendly: the entire pipeline is non-blocking
     */
    fun activityFeedForTask(taskId: String): Flow<String> = flow {
        val events = eventStore.load(taskId)
        for (event in events) {
            emit(event)
        }
    }.map { event -> formatEvent(event) }

    /**
     * Returns a Flow that filters events by type, demonstrating the `filter` operator.
     *
     * Uses reified-style filtering via `filterIsInstance` — a Kotlin stdlib
     * extension that leverages inline + reified under the hood.
     */
    inline fun <reified T : DomainEvent> eventsOfType(): Flow<T> =
        replayAllEvents().filterIsInstance<T>()

    /**
     * Returns a Flow that polls the event store periodically for new events.
     *
     * Demonstrates:
     * - `flow { }` builder with `delay` for periodic polling
     * - Flow's natural backpressure: if the collector is slow, emission suspends
     * - Cold stream: polling only starts when collected
     */
    fun pollingFeed(intervalMillis: Long = 1000L): Flow<DomainEvent> = flow {
        var lastSeenCount = 0
        while (true) {
            val allEvents = eventStore.loadAll()
            val newEvents = allEvents.drop(lastSeenCount)
            for (event in newEvents) {
                emit(event)
            }
            lastSeenCount = allEvents.size
            delay(intervalMillis)
        }
    }

    /**
     * Returns a Flow with logging side effects using `onEach`.
     *
     * `onEach` is a transparent operator — it performs a side effect on each
     * element without changing the stream. Useful for logging/metrics.
     */
    fun replayWithLogging(logger: (String) -> Unit): Flow<DomainEvent> =
        replayAllEvents().onEach { event ->
            logger("Processing event: ${event::class.simpleName} for ${event.aggregateId}")
        }

    private fun formatEvent(event: DomainEvent): String = when (event) {
        is DomainEvent.TaskCreated -> "Task '${event.title}' created"
        is DomainEvent.TaskAssigned -> "Task assigned to ${event.assigneeId.value}"
        is DomainEvent.TaskCompleted -> "Task completed at ${event.completedAt}"
        is DomainEvent.TaskPriorityChanged -> "Priority changed from ${event.oldPriority} to ${event.newPriority}"
        is DomainEvent.TagAddedToTask -> "Tag '${event.tag}' added"
        is DomainEvent.TaskMovedToProject -> "Task moved to project ${event.projectId.value}"
    }
}
