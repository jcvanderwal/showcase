package com.showcase.kotlin.domain.store

import com.showcase.kotlin.domain.event.DomainEvent
import java.util.concurrent.ConcurrentHashMap

interface EventStore {
    suspend fun append(aggregateId: String, events: List<DomainEvent>)
    suspend fun load(aggregateId: String): List<DomainEvent>
    suspend fun loadAll(): List<DomainEvent>
}

class InMemoryEventStore : EventStore {
    private val store = ConcurrentHashMap<String, MutableList<DomainEvent>>()

    override suspend fun append(aggregateId: String, events: List<DomainEvent>) {
        store.computeIfAbsent(aggregateId) { mutableListOf() }.addAll(events)
    }

    override suspend fun load(aggregateId: String): List<DomainEvent> {
        return store[aggregateId]?.toList() ?: emptyList()
    }

    override suspend fun loadAll(): List<DomainEvent> {
        return store.values.flatMap { it.toList() }
    }
}
