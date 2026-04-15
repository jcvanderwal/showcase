package com.showcase.kotlin.slice.delegation

import com.showcase.kotlin.domain.event.DomainEvent
import com.showcase.kotlin.domain.store.EventStore

/**
 * Slice 6 — Delegation Pattern (Req 7)
 *
 * Demonstrates Kotlin's `by` keyword for class delegation.
 * LoggingEventStore delegates the entire EventStore interface to [delegate],
 * overriding only the methods where logging is needed.
 * In Java, every method must be manually forwarded.
 */
class LoggingEventStore(
    private val delegate: EventStore,
    private val log: (String) -> Unit = ::println
) : EventStore by delegate {

    override suspend fun append(aggregateId: String, events: List<DomainEvent>) {
        log("Appending ${events.size} event(s) for aggregate '$aggregateId'")
        delegate.append(aggregateId, events)
        log("Append complete for aggregate '$aggregateId'")
    }

    override suspend fun load(aggregateId: String): List<DomainEvent> {
        log("Loading events for aggregate '$aggregateId'")
        val events = delegate.load(aggregateId)
        log("Loaded ${events.size} event(s) for aggregate '$aggregateId'")
        return events
    }
}
