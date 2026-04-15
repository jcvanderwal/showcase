package com.showcase.java.domain.store;

import com.showcase.java.domain.event.DomainEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryEventStore implements EventStore {

    private final ConcurrentHashMap<String, List<DomainEvent>> store = new ConcurrentHashMap<>();

    @Override
    public void append(String aggregateId, List<DomainEvent> events) {
        store.computeIfAbsent(aggregateId, _ -> new ArrayList<>()).addAll(events);
    }

    @Override
    public List<DomainEvent> load(String aggregateId) {
        var events = store.get(aggregateId);
        return events != null ? List.copyOf(events) : List.of();
    }

    @Override
    public List<DomainEvent> loadAll() {
        return store.values().stream()
                .flatMap(List::stream)
                .toList();
    }
}
