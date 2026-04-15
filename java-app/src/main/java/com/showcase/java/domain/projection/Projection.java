package com.showcase.java.domain.projection;

import com.showcase.java.domain.event.DomainEvent;

public interface Projection {
    void handle(DomainEvent event);
}
