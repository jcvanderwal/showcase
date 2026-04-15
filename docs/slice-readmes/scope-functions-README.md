# Scope Functions and Idiomatic Constructs

**Convergence Matrix Tier: Gap narrowed**

## Overview

This slice compares Kotlin's scope functions (`let`, `run`, `with`, `apply`, `also`) and data class features (`copy()`, destructuring) against Java 25's records, record patterns, deconstruction, and `var` in event processing workflows.

## Kotlin Approach

Kotlin scope functions reduce ceremony in everyday event processing code:

```kotlin
// apply for aggregate initialization
val projection = TaskSummaryProjection().apply {
    subscribe(eventBus)
    replayFrom(eventStore)
}

// let for nullable transformation
event.dueDate?.let { formatDate(it) }

// also for logging side effects
eventStore.append(id, events).also { log.info("Stored ${events.size} events") }

// copy() for immutable event modification
val updated = event.copy(priority = Priority.HIGH)

// Destructuring
val (aggregateId, timestamp, title) = taskCreatedEvent
```

## Java 25 Approach

Java 25 records with deconstruction patterns provide similar capabilities for data handling:

```java
// Record patterns for deconstruction (Java 21+)
if (event instanceof DomainEvent.TaskCreated(var id, var ts, var title, _, _, _, _)) {
    log.info("Task created: " + title + " at " + ts);
}

// var for local type inference
var projection = new TaskSummaryProjection();
var events = eventStore.loadAll();

// Records provide equals, hashCode, toString, accessors
record TaskSnapshot(String id, String title, Priority priority) {}
```

## Honest Comparison

| Aspect | Kotlin | Java 25 |
|--------|--------|---------|
| Object configuration | `apply { ... }` | Constructor + setters |
| Nullable transformation | `let { ... }` | `Optional.map()` |
| Side effects | `also { ... }` | Separate statement |
| Immutable modification | `copy(field = newValue)` | Manual `new Record(...)` |
| Destructuring | `val (a, b, c) = obj` | Record patterns in `instanceof` |
| Data class features | `copy`, `componentN`, `toString` | Record accessors, `toString` |
| Default parameters | Supported in data classes | Not supported in records |

Java 25 records with deconstruction patterns have meaningfully narrowed the gap with Kotlin data classes. Record patterns in `instanceof` and `switch` provide powerful deconstruction. However, Kotlin retains advantages in `copy()` for immutable modification (Java requires manually constructing a new record), `componentN()` flexibility, and default parameter values in data classes.

## Source Files

- **Kotlin**: `kotlin-app/src/main/kotlin/com/showcase/kotlin/slice/scopefunctions/`
  - `ScopeFunctionShowcase.kt` — `let`/`run`/`with`/`apply`/`also`, `copy()`, destructuring
- **Java**: `java-app/src/main/java/com/showcase/java/slice/javapatterns/`
  - `JavaPatternShowcase.java` — records, record patterns, deconstruction, `var`
