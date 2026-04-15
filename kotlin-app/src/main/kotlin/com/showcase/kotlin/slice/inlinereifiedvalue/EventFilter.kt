package com.showcase.kotlin.slice.inlinereifiedvalue

import com.showcase.kotlin.domain.event.DomainEvent

/**
 * Slice 9 — Inline & Reified Generics: EventFilter
 *
 * Demonstrates Kotlin's reified type parameters in inline functions
 * for type-safe event filtering without passing Class tokens.
 *
 * Java cannot do this — type erasure means you must pass an explicit
 * Class<T> token to filter by type at runtime.
 */

/**
 * Filters domain events by reified type parameter.
 * No Class token needed — the type is preserved at the call site via inlining.
 *
 * Usage: val created = filterEvents<DomainEvent.TaskCreated>(events)
 */
inline fun <reified T : DomainEvent> filterEvents(events: List<DomainEvent>): List<T> =
    events.filterIsInstance<T>()

/**
 * Counts events of a specific type using reified generics.
 *
 * Usage: val count = countEvents<DomainEvent.TaskAssigned>(events)
 */
inline fun <reified T : DomainEvent> countEvents(events: List<DomainEvent>): Int =
    events.count { it is T }

/**
 * Finds the first event of a specific type, or null if none exists.
 *
 * Usage: val first = findFirstEvent<DomainEvent.TaskCompleted>(events)
 */
inline fun <reified T : DomainEvent> findFirstEvent(events: List<DomainEvent>): T? =
    events.filterIsInstance<T>().firstOrNull()
