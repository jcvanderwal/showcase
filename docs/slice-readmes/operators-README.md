# Operator Overloading for Domain Expressiveness

**Convergence Matrix Tier: Kotlin still leads**

## Overview

This slice compares Kotlin's operator overloading against Java 25's named methods for domain operations like merging tag sets, checking event membership, comparing priorities, and invoking commands.

## Kotlin Approach

Kotlin allows overloading standard operators on domain types:

```kotlin
// plus (+) for merging tag sets
operator fun Set<String>.plus(other: Set<String>): Set<String> = this.union(other)

// contains (in) for event stream membership
operator fun List<DomainEvent>.contains(event: DomainEvent): Boolean =
    any { it.aggregateId == event.aggregateId && it.timestamp == event.timestamp }

// invoke for command execution
operator fun CommandHandler.invoke(command: Command) = handle(command)

// Usage reads naturally
val merged = taskATags + taskBTags
val exists = event in eventStream
val events = handler(Command.CreateTask(...))
```

## Java 25 Approach

Java 25 has no operator overloading. Named methods serve the same purpose:

```java
// Named methods for the same operations
Set<String> merged = TagSetOps.mergeTags(taskATags, taskBTags);
boolean exists = EventStreamOps.contains(eventStream, event);
List<DomainEvent> events = CommandExecutor.execute(handler, command);
```

## Honest Comparison

| Aspect | Kotlin | Java 25 |
|--------|--------|---------|
| Tag set merging | `tags1 + tags2` | `TagSetOps.mergeTags(tags1, tags2)` |
| Event membership | `event in stream` | `EventStreamOps.contains(stream, event)` |
| Command execution | `handler(command)` | `CommandExecutor.execute(handler, command)` |
| Readability | Concise, domain-natural | Explicit, self-documenting |
| Abuse potential | Can obscure intent if misused | N/A |

Operator overloading is a double-edged sword. When used judiciously on domain types (tag sets, event streams), it makes code read more naturally. When overused, it can obscure intent. Java 25 has no operator overloading at all — named methods are always explicit, which some teams prefer. This is a genuine Kotlin advantage for teams that value expressiveness.

## Java Ecosystem Workaround: Manifold

[Manifold](https://manifold.systems/) (version 2026.1.6, fully supports JDK 8–25) adds operator overloading to Java via compiler plugin. You implement conventionally-named methods (`plus()`, `compareTo()`, `contains()`, etc.) and Manifold lets you use the corresponding operators (`+`, `>`, `in`) in Java source code:

```java
// With Manifold, implement plus() and use + operator in Java
public Set<String> plus(Set<String> other) { return Stream.concat(...).collect(...); }
// Usage: var merged = tags1 + tags2;
```

This gives Java the same operator syntax as Kotlin for domain types. The tradeoff is adding a non-standard compiler plugin. IntelliJ support is provided via the Manifold IDE plugin.

## Source Files

- **Kotlin**: `kotlin-app/src/main/kotlin/com/showcase/kotlin/slice/operators/`
  - `TagSetOperators.kt` — `plus` for tag set merging
  - `EventStreamOperators.kt` — `contains` for event membership
  - `CommandInvoke.kt` — `invoke` for command execution
- **Java**: `java-app/src/main/java/com/showcase/java/slice/namedmethods/`
  - `TagSetOps.java` — `mergeTags()` named method
  - `EventStreamOps.java` — `contains()` named method
  - `CommandExecutor.java` — `execute()` named method
