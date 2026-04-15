package com.showcase.java.domain.store;

import com.showcase.java.domain.event.DomainEvent;

import java.util.List;

public interface EventStore {
    void append(String aggregateId, List<DomainEvent> events);
    List<DomainEvent> load(String aggregateId);
    List<DomainEvent> loadAll();
}
