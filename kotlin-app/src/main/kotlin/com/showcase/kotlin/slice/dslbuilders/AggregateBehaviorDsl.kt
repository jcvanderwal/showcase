package com.showcase.kotlin.slice.dslbuilders

import com.showcase.kotlin.domain.command.Command
import com.showcase.kotlin.domain.event.DomainEvent

/**
 * Slice 5 — DSL Builders (Req 6)
 *
 * DSL for defining aggregate behavior: command-to-event mappings and
 * event application rules. Uses lambdas with receivers so the configuration
 * reads like a declarative specification.
 */

/** Immutable result describing aggregate behavior. */
data class AggregateBehavior(
    val commandHandlers: Map<String, (Command) -> List<DomainEvent>>,
    val eventAppliers: Map<String, (DomainEvent) -> Unit>
)

/** Top-level DSL entry point. */
fun aggregateBehavior(block: AggregateBehaviorBuilder.() -> Unit): AggregateBehavior {
    return AggregateBehaviorBuilder().apply(block).build()
}

@ProjectDslMarker
class AggregateBehaviorBuilder {
    @PublishedApi
    internal val commandHandlers = mutableMapOf<String, (Command) -> List<DomainEvent>>()
    @PublishedApi
    internal val eventAppliers = mutableMapOf<String, (DomainEvent) -> Unit>()

    /** Register a handler that maps a command type to domain events. */
    inline fun <reified C : Command> onCommand(noinline handler: (C) -> List<DomainEvent>) {
        val key = C::class.simpleName ?: error("Cannot resolve command class name")
        @Suppress("UNCHECKED_CAST")
        commandHandlers[key] = handler as (Command) -> List<DomainEvent>
    }

    /** Register an applier that reacts to a specific event type. */
    inline fun <reified E : DomainEvent> onEvent(noinline applier: (E) -> Unit) {
        val key = E::class.simpleName ?: error("Cannot resolve event class name")
        @Suppress("UNCHECKED_CAST")
        eventAppliers[key] = applier as (DomainEvent) -> Unit
    }

    fun build(): AggregateBehavior {
        return AggregateBehavior(
            commandHandlers.toMap(),
            eventAppliers.toMap()
        )
    }
}
