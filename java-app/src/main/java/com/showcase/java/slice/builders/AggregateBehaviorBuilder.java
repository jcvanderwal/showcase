package com.showcase.java.slice.builders;

import com.showcase.java.domain.command.Command;
import com.showcase.java.domain.event.DomainEvent;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Slice 5 — Fluent Builders (Req 6)
 *
 * Java 25 equivalent of the Kotlin AggregateBehaviorDsl.
 * Uses a fluent builder with explicit Class tokens for type-safe
 * command-to-event and event applier registration.
 */
public final class AggregateBehaviorBuilder {

    /** Immutable result describing aggregate behavior. */
    public record AggregateBehavior(
            Map<String, Function<Command, List<DomainEvent>>> commandHandlers,
            Map<String, Consumer<DomainEvent>> eventAppliers
    ) {
        public AggregateBehavior {
            commandHandlers = Map.copyOf(commandHandlers);
            eventAppliers = Map.copyOf(eventAppliers);
        }
    }

    private final Map<String, Function<Command, List<DomainEvent>>> commandHandlers = new LinkedHashMap<>();
    private final Map<String, Consumer<DomainEvent>> eventAppliers = new LinkedHashMap<>();

    /**
     * Register a handler that maps a command type to domain events.
     */
    @SuppressWarnings("unchecked")
    public <C extends Command> AggregateBehaviorBuilder onCommand(
            Class<C> commandType,
            Function<C, List<DomainEvent>> handler
    ) {
        commandHandlers.put(
                commandType.getSimpleName(),
                cmd -> handler.apply((C) cmd)
        );
        return this;
    }

    /**
     * Register an applier that reacts to a specific event type.
     */
    @SuppressWarnings("unchecked")
    public <E extends DomainEvent> AggregateBehaviorBuilder onEvent(
            Class<E> eventType,
            Consumer<E> applier
    ) {
        eventAppliers.put(
                eventType.getSimpleName(),
                evt -> applier.accept((E) evt)
        );
        return this;
    }

    public AggregateBehavior build() {
        return new AggregateBehavior(commandHandlers, eventAppliers);
    }
}
