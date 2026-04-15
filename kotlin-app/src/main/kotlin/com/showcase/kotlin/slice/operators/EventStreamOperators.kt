package com.showcase.kotlin.slice.operators

import com.showcase.kotlin.domain.event.DomainEvent

/**
 * Slice 7 — Operator Overloading: EventStreamOperators
 *
 * Demonstrates Kotlin's operator overloading with the `contains` (in) operator
 * for checking if a DomainEvent exists in an event stream. Java requires
 * explicit named methods like `contains()` for the same operation.
 */

/**
 * A wrapper around a list of domain events that supports the `in` operator.
 * Usage: val exists = someEvent in eventStream
 */
class EventStream(private val events: List<DomainEvent>) {

    /**
     * Overloads the `in` operator to check if a specific event exists in the stream.
     * Matches by aggregateId and timestamp for identity.
     */
    operator fun contains(event: DomainEvent): Boolean =
        events.any { it.aggregateId == event.aggregateId && it.timestamp == event.timestamp }

    /**
     * Overloads the `in` operator to check if any event for a given aggregateId exists.
     */
    operator fun contains(aggregateId: String): Boolean =
        events.any { it.aggregateId == aggregateId }

    fun toList(): List<DomainEvent> = events.toList()

    val size: Int get() = events.size
}
