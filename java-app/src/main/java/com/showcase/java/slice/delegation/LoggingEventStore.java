package com.showcase.java.slice.delegation;

import com.showcase.java.domain.event.DomainEvent;
import com.showcase.java.domain.store.EventStore;

import java.util.List;
import java.util.function.Consumer;

/**
 * Slice 6 — Delegation Pattern (Req 7)
 *
 * Java 25 equivalent of Kotlin's {@code by} keyword delegation.
 * Every interface method must be manually forwarded to the delegate.
 * In Kotlin, only the overridden methods need explicit code.
 */
public class LoggingEventStore implements EventStore {

    private final EventStore delegate;
    private final Consumer<String> log;

    public LoggingEventStore(EventStore delegate) {
        this(delegate, System.out::println);
    }

    public LoggingEventStore(EventStore delegate, Consumer<String> log) {
        this.delegate = delegate;
        this.log = log;
    }

    @Override
    public void append(String aggregateId, List<DomainEvent> events) {
        log.accept("Appending %d event(s) for aggregate '%s'".formatted(events.size(), aggregateId));
        delegate.append(aggregateId, events);
        log.accept("Append complete for aggregate '%s'".formatted(aggregateId));
    }

    @Override
    public List<DomainEvent> load(String aggregateId) {
        log.accept("Loading events for aggregate '%s'".formatted(aggregateId));
        var events = delegate.load(aggregateId);
        log.accept("Loaded %d event(s) for aggregate '%s'".formatted(events.size(), aggregateId));
        return events;
    }

    // Must manually delegate — Java has no `by` keyword
    @Override
    public List<DomainEvent> loadAll() {
        log.accept("Loading all events");
        var events = delegate.loadAll();
        log.accept("Loaded %d total event(s)".formatted(events.size()));
        return events;
    }
}
