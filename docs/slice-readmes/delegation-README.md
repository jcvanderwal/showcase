# Delegation Pattern

**Convergence Matrix Tier: Kotlin still leads**

## Overview

This slice compares Kotlin's built-in delegation (`by` keyword, property delegates) against Java 25's manual delegation for implementing event store decorators and audit logging in the Task Manager domain.

## Kotlin Approach

Kotlin's `by` keyword delegates interface implementation to a wrapped instance in a single line. Property delegates (`lazy`, `observable`, custom) handle cross-cutting concerns declaratively:

```kotlin
// Class delegation — one line, all methods forwarded
class LoggingEventStore(
    private val delegate: EventStore
) : EventStore by delegate {
    override suspend fun append(aggregateId: String, events: List<DomainEvent>) {
        log.info("Appending ${events.size} events for $aggregateId")
        delegate.append(aggregateId, events)
    }
}

// Property delegation
var lastModified: Instant by observable(Instant.now()) { _, old, new ->
    auditLog.record("lastModified changed from $old to $new")
}
```

## Java 25 Approach

Java 25 requires manually delegating every interface method:

```java
public class LoggingEventStore implements EventStore {
    private final EventStore delegate;

    @Override
    public void append(String aggregateId, List<DomainEvent> events) {
        log.info("Appending " + events.size() + " events for " + aggregateId);
        delegate.append(aggregateId, events);
    }

    @Override
    public List<DomainEvent> load(String aggregateId) {
        return delegate.load(aggregateId);  // manual forwarding
    }

    @Override
    public List<DomainEvent> loadAll() {
        return delegate.loadAll();  // manual forwarding
    }
}
```

## Honest Comparison

| Aspect | Kotlin | Java 25 |
|--------|--------|---------|
| Class delegation | `by` keyword (one line) | Manual forwarding of every method |
| Adding new interface methods | Automatically delegated | Must add forwarding manually |
| Property delegates | `lazy`, `observable`, custom | No equivalent |
| Maintenance burden | Low — only override what changes | High — every method must be forwarded |
| Lines of code | ~5 for a decorator | ~15+ for the same decorator |

Java 25 has no built-in delegation mechanism. Every interface method must be manually forwarded, and adding a new method to the interface requires updating every decorator. Kotlin's `by` keyword eliminates this boilerplate entirely — you only override the methods you want to customize.

## Source Files

- **Kotlin**: `kotlin-app/src/main/kotlin/com/showcase/kotlin/slice/delegation/`
  - `LoggingEventStore.kt` — `by` keyword delegation
  - `AuditDelegate.kt` — `lazy`, `observable`, custom property delegates
- **Java**: `java-app/src/main/java/com/showcase/java/slice/delegation/`
  - `LoggingEventStore.java` — manual delegation
  - `AuditWrapper.java` — manual audit wrapper
