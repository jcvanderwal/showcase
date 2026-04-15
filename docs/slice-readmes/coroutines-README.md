# Coroutines vs Virtual Threads: Async Event Processing

**Convergence Matrix Tier: Gap narrowed**

## Overview

This slice compares Kotlin coroutines (structured concurrency, Flow, Channels) against Java 25 virtual threads (`StructuredTaskScope`) for asynchronous event processing and projection updates in the Task Manager domain.

## Kotlin Approach

Kotlin provides coroutines with `suspend` functions, structured concurrency scopes, Flow for reactive streams, and Channels for inter-component communication:

```kotlin
// Structured concurrency with partial failure tolerance
supervisorScope {
    projections.map { projection ->
        async { projection.handle(event) }
    }.awaitAll()
}

// Flow-based reactive event streaming
fun activityFeed(): Flow<DomainEvent> = flow {
    eventStore.loadAll().forEach { emit(it) }
}

// Channel-based decoupled communication
val channel = Channel<DomainEvent>(Channel.BUFFERED)
```

## Java 25 Approach

Java 25 provides virtual threads and `StructuredTaskScope` for lightweight concurrency:

```java
// Structured concurrency with virtual threads
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    projections.stream()
        .map(p -> scope.fork(() -> { p.handle(event); return null; }))
        .toList();
    scope.join().throwIfFailed();
}
```

## Honest Comparison

| Aspect | Kotlin Coroutines | Java 25 Virtual Threads |
|--------|-------------------|------------------------|
| Basic concurrency | `coroutineScope` + `async` | `StructuredTaskScope` + `fork` |
| Partial failure | `supervisorScope` | `ShutdownOnSuccess` (different model) |
| Reactive streams | `Flow` (cold), `SharedFlow` (hot) | No built-in equivalent |
| Backpressure | Built into Flow/Channel | Manual with queues |
| Inter-component comms | `Channel` (typed, buffered) | `BlockingQueue` / manual |
| Cancellation | Cooperative, scope-based | Thread interruption |
| Suspend functions | First-class, composable | N/A |

Java 25 virtual threads with `StructuredTaskScope` provide a competitive alternative for straightforward concurrent task execution — the basic "fan-out to projections" pattern is well-served. However, Kotlin coroutines still lead for reactive streaming (Flow), channel-based communication, coroutine context propagation, and the composability of `suspend` functions.

## Source Files

- **Kotlin**: `kotlin-app/src/main/kotlin/com/showcase/kotlin/slice/coroutines/`
  - `CoroutineEventProcessor.kt` — `coroutineScope`, `supervisorScope`, `async`
  - `FlowActivityFeed.kt` — Flow-based reactive event streaming
  - `ChannelEventBus.kt` — Channel-based event distribution
- **Java**: `java-app/src/main/java/com/showcase/java/slice/virtualthreads/`
  - `VirtualThreadEventProcessor.java` — virtual thread event processing
  - `StructuredTaskScopeProcessor.java` — `StructuredTaskScope` for structured concurrency
