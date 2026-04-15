package com.showcase.java.slice.utilitymethods;

import com.showcase.java.domain.event.DomainEvent;

import java.time.Instant;
import java.util.List;

/**
 * Slice 4 — Utility Methods (Req 5)
 *
 * Java 25 equivalent of Kotlin extension functions on List&lt;DomainEvent&gt;.
 * Static utility methods — the caller must discover this class independently.
 */
public final class EventListUtils {

    private EventListUtils() {}

    /**
     * Filters domain events to only those belonging to the given aggregate (task) ID.
     */
    public static List<DomainEvent> filterByTaskId(List<DomainEvent> events, String taskId) {
        return events.stream()
                .filter(e -> e.aggregateId().equals(taskId))
                .toList();
    }

    /**
     * Returns the most recent timestamp across all events, or null if the list is empty.
     */
    public static Instant latestTimestamp(List<DomainEvent> events) {
        return events.stream()
                .map(DomainEvent::timestamp)
                .max(Instant::compareTo)
                .orElse(null);
    }
}
