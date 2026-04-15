package com.showcase.java.slice.genericsrecords;

import com.showcase.java.domain.event.DomainEvent;

import java.util.List;

/**
 * Slice 9 — Generics & Records: EventFilter
 *
 * Java equivalent of Kotlin's reified inline function for event filtering.
 * Due to type erasure, Java must use an explicit Class&lt;T&gt; token parameter
 * to filter events by type at runtime.
 *
 * Kotlin equivalent: inline fun &lt;reified T : DomainEvent&gt; filterEvents(events)
 * — no Class token needed thanks to reified generics.
 */
public final class EventFilter {

    private EventFilter() {}

    /**
     * Filters domain events by type using an explicit Class token.
     * Kotlin equivalent: filterEvents&lt;DomainEvent.TaskCreated&gt;(events)
     *
     * @param events the list of events to filter
     * @param type   the Class token for the desired event type
     * @param <T>    the event type to filter for
     * @return a list containing only events of the specified type
     */
    public static <T extends DomainEvent> List<T> filterEvents(
            List<DomainEvent> events, Class<T> type) {
        return events.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .toList();
    }

    /**
     * Counts events of a specific type.
     * Kotlin equivalent: countEvents&lt;DomainEvent.TaskAssigned&gt;(events)
     */
    public static <T extends DomainEvent> long countEvents(
            List<DomainEvent> events, Class<T> type) {
        return events.stream()
                .filter(type::isInstance)
                .count();
    }

    /**
     * Finds the first event of a specific type, or null if none exists.
     * Kotlin equivalent: findFirstEvent&lt;DomainEvent.TaskCompleted&gt;(events)
     */
    public static <T extends DomainEvent> T findFirstEvent(
            List<DomainEvent> events, Class<T> type) {
        return events.stream()
                .filter(type::isInstance)
                .map(type::cast)
                .findFirst()
                .orElse(null);
    }
}
