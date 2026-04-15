# Inline Functions, Reified Generics, and Value Classes

**Convergence Matrix Tier: Kotlin still leads**

## Overview

This slice compares Kotlin's inline functions with reified generics and value classes against Java 25's generics (with type erasure) and records for type-safe domain identifiers and event filtering.

## Kotlin Approach

Kotlin's `inline` functions preserve type information at runtime via `reified`, and value classes provide zero-overhead type wrappers:

```kotlin
// Reified generics — type preserved at runtime
inline fun <reified T : DomainEvent> EventStore.filterEvents(): List<T> =
    loadAll().filterIsInstance<T>()

// Usage — no class token needed
val taskCreatedEvents = eventStore.filterEvents<DomainEvent.TaskCreated>()

// Value classes — zero runtime overhead
@JvmInline value class TaskId(val value: String)
@JvmInline value class ProjectId(val value: String)

// Compile-time type safety, runtime String
fun assign(taskId: TaskId, userId: UserId) // can't accidentally swap
```

## Java 25 Approach

Java 25 generics suffer from type erasure, requiring explicit class tokens. Records provide type-safe wrappers but with runtime allocation:

```java
// Class token workaround for type erasure
public static <T extends DomainEvent> List<T> filterEvents(
        EventStore store, Class<T> eventType) {
    return store.loadAll().stream()
        .filter(eventType::isInstance)
        .map(eventType::cast)
        .toList();
}

// Usage — must pass class token
var events = EventFilter.filterEvents(store, DomainEvent.TaskCreated.class);

// Records as type-safe wrappers (runtime allocation)
public record TaskId(String value) {}
public record ProjectId(String value) {}
```

## Honest Comparison

| Aspect | Kotlin | Java 25 |
|--------|--------|---------|
| Generic type at runtime | `reified` preserves type | Type erasure — need `Class<T>` token |
| Event filtering API | `filterEvents<TaskCreated>()` | `filterEvents(store, TaskCreated.class)` |
| Type-safe IDs | Value class (zero overhead) | Record (heap allocation) |
| ID mixing prevention | Compile-time | Compile-time |
| Runtime cost of IDs | None (inlined to `String`) | Object allocation per instance |

Java 25 records partially address the value class use case — they provide type safety for identifiers. However, records allocate on the heap while Kotlin value classes are inlined to their underlying type at runtime. For reified generics, Java has no equivalent — type erasure is a fundamental JVM limitation that Kotlin works around via inlining.

## Java Ecosystem Workaround: JEP 401 (Upcoming)

For value classes: [JEP 401: Value Classes and Objects (Preview)](https://openjdk.org/jeps/401) is part of Project Valhalla and has an [early-access build](https://jdk.java.net/valhalla/) available. It would give Java identity-free, potentially zero-overhead value classes similar to Kotlin's `@JvmInline value class`. Not in JDK 25 yet, but expected in a future release.

For reified generics: no solution exists or is planned. The Valhalla team has consciously chosen to keep type erasure (see Brian Goetz's "In Defense of Erasure" design note). The `Class<T>` token pattern and `@SuppressWarnings("unchecked")` remain the standard Java workaround.

## Source Files

- **Kotlin**: `kotlin-app/src/main/kotlin/com/showcase/kotlin/slice/inlinereifiedvalue/`
  - `EventFilter.kt` — reified inline function for type-safe event filtering
  - `TypeSafeIds.kt` — value classes for domain identifiers
- **Java**: `java-app/src/main/java/com/showcase/java/slice/genericsrecords/`
  - `EventFilter.java` — class-token workaround for event filtering
  - `TypeSafeIds.java` — record-based identifier wrappers
