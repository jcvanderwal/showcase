package com.showcase.java.domain.bus;

import com.showcase.java.domain.event.DomainEvent;

import java.util.List;
import java.util.function.Consumer;

public interface EventBus {
    void publish(List<DomainEvent> events);
    void subscribe(Consumer<DomainEvent> handler);
}
