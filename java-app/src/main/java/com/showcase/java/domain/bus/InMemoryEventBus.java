package com.showcase.java.domain.bus;

import com.showcase.java.domain.event.DomainEvent;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class InMemoryEventBus implements EventBus {

    private final CopyOnWriteArrayList<Consumer<DomainEvent>> handlers = new CopyOnWriteArrayList<>();

    @Override
    public void publish(List<DomainEvent> events) {
        for (var event : events) {
            for (var handler : handlers) {
                handler.accept(event);
            }
        }
    }

    @Override
    public void subscribe(Consumer<DomainEvent> handler) {
        handlers.add(handler);
    }
}
