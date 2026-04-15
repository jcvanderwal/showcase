package com.showcase.java.slice.namedmethods;

import com.showcase.java.domain.event.DomainEvent;

import java.util.List;

/**
 * Slice 7 — Named Methods: EventStreamOps
 *
 * Java equivalent of Kotlin's `contains` (in) operator overloading for event streams.
 * Since Java has no operator overloading, we use explicit named methods
 * like {@code contains()} instead of the {@code in} operator.
 */
public final class EventStreamOps {

    private final List<DomainEvent> events;

    public EventStreamOps(List<DomainEvent> events) {
        this.events = List.copyOf(events);
    }

    /**
     * Checks if a specific event exists in the stream by aggregateId and timestamp.
     * Kotlin equivalent: someEvent in eventStream
     */
    public boolean contains(DomainEvent event) {
        return events.stream()
                .anyMatch(e -> e.aggregateId().equals(event.aggregateId())
                        && e.timestamp().equals(event.timestamp()));
    }

    /**
     * Checks if any event for a given aggregateId exists in the stream.
     * Kotlin equivalent: aggregateId in eventStream
     */
    public boolean containsAggregate(String aggregateId) {
        return events.stream()
                .anyMatch(e -> e.aggregateId().equals(aggregateId));
    }

    public List<DomainEvent> toList() {
        return events;
    }

    public int size() {
        return events.size();
    }
}
