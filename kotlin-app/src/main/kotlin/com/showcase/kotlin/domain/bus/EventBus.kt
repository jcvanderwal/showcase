package com.showcase.kotlin.domain.bus

import com.showcase.kotlin.domain.event.DomainEvent
import java.util.concurrent.CopyOnWriteArrayList

interface EventBus {
    suspend fun publish(events: List<DomainEvent>)
    fun subscribe(handler: suspend (DomainEvent) -> Unit)
}

class InMemoryEventBus : EventBus {
    private val handlers = CopyOnWriteArrayList<suspend (DomainEvent) -> Unit>()

    override suspend fun publish(events: List<DomainEvent>) {
        for (event in events) {
            for (handler in handlers) {
                handler(event)
            }
        }
    }

    override fun subscribe(handler: suspend (DomainEvent) -> Unit) {
        handlers.add(handler)
    }
}
