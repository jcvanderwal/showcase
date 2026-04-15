# Sealed Classes: Commands, Events, and Exhaustive Handling

**Convergence Matrix Tier: Java 25 at parity**

## Overview

This slice compares Kotlin sealed classes with `when` expressions against Java 25 sealed interfaces with records and `switch` pattern matching for modeling commands, events, and task status in an event-sourced domain.

## Kotlin Approach

Kotlin uses sealed class hierarchies with `data class` variants and exhaustive `when` expressions:

```kotlin
sealed class Command {
    data class CreateTask(val taskId: TaskId, val title: String, ...) : Command()
    data class AssignTask(val taskId: TaskId, val assigneeId: UserId) : Command()
    // ...
}

// Exhaustive when — compiler enforces all branches
val events = when (command) {
    is Command.CreateTask -> handleCreate(command)  // smart cast
    is Command.AssignTask -> handleAssign(command)
    // ...
}
```

## Java 25 Approach

Java 25 uses sealed interfaces with record variants and exhaustive `switch` with pattern matching:

```java
public sealed interface Command {
    record CreateTask(TaskId taskId, String title, ...) implements Command {}
    record AssignTask(TaskId taskId, UserId assigneeId) implements Command {}
    // ...
}

// Exhaustive switch — compiler enforces all branches
var events = switch (command) {
    case Command.CreateTask c -> handleCreate(c);
    case Command.AssignTask c -> handleAssign(c);
    // ...
};
```

## Honest Comparison

| Aspect | Kotlin | Java 25 |
|--------|--------|---------|
| Sealed hierarchy definition | `sealed class` + `data class` | `sealed interface` + `record` |
| Exhaustiveness checking | `when` expression (compile-time) | `switch` expression (compile-time) |
| Smart casts in branches | Yes — `command` auto-cast to subtype | Requires binding variable (`c`) |
| Data-carrying variants | `data class` (copy, equals, hashCode) | `record` (equals, hashCode, accessors) |
| Syntax conciseness | Slightly more concise | Very close, marginally more verbose |

Java 25's sealed interfaces + records + exhaustive switch pattern matching have reached near-parity with Kotlin's sealed classes + when. The remaining Kotlin advantages are minor: smart casts avoid the need for a binding variable, and `when` as an expression feels slightly more natural. These are ergonomic differences, not capability gaps.

## Source Files

- **Kotlin**: `kotlin-app/src/main/kotlin/com/showcase/kotlin/slice/sealedclasses/`
  - `KotlinCommandHandler.kt` — exhaustive `when` for command dispatch
  - `KotlinEventApplier.kt` — exhaustive `when` for event application
- **Java**: `java-app/src/main/java/com/showcase/java/slice/sealedclasses/`
  - `JavaCommandHandler.java` — exhaustive `switch` for command dispatch
  - `JavaEventApplier.java` — exhaustive `switch` for event application
- **Domain**: Both apps define sealed hierarchies in `domain/command/` and `domain/event/`
