# Null Safety at the Type System Level

**Convergence Matrix Tier: Kotlin still leads**

## Overview

This slice compares Kotlin's compile-time null safety against Java 25's runtime-based null handling in an event-sourced task management workflow involving nullable fields like `dueDate`, `description`, and `assignee`.

## Kotlin Approach

Kotlin encodes nullability directly in the type system. The compiler rejects null assignments to non-nullable types and provides operators for safe navigation:

```kotlin
// Types declare intent — compiler enforces it
val task: TaskSummary = findTask(id) ?: throw TaskNotFound(id)
val label = task.dueDate?.let { "Due: $it" } ?: "No due date"

// Chained safe calls with scope functions
task.assignee
    ?.let { loadUser(it) }
    ?.also { log.info("Assigned to ${it.name}") }
    ?: log.warn("Unassigned task")
```

## Java 25 Approach

Java 25 uses `Optional`, `@Nullable` annotations, and null-pattern `switch` for null handling — all enforced at runtime:

```java
// Optional wrapping
Optional.ofNullable(task.dueDate())
    .map(d -> "Due: " + d)
    .orElse("No due date");

// Null-pattern in switch (Java 21+)
switch (task.assignee()) {
    case UserId uid -> log.info("Assigned to " + uid);
    case null -> log.warn("Unassigned task");
}
```

## Honest Comparison

| Aspect | Kotlin | Java 25 |
|--------|--------|---------|
| Null safety enforcement | Compile-time | Runtime only |
| Nullable type declaration | `String?` in type system | `@Nullable` annotation (advisory) |
| Safe navigation | `?.` operator (chained) | `Optional.map()` chains |
| Default values | `?:` elvis operator | `Optional.orElse()` / `Objects.requireNonNullElse()` |
| Null in pattern matching | N/A (nulls caught at compile time) | `case null ->` in switch |
| NPE risk | Near-zero (platform types aside) | Reduced but still present |

Kotlin's compile-time null safety remains a genuine advantage. Java 25's null-pattern `switch` and improved `Optional` ergonomics have made null handling less painful, but they cannot prevent null-related bugs at compile time. A `@Nullable` annotation is a hint; Kotlin's `?` is a guarantee.

## Java Ecosystem Workaround: JSpecify + NullAway

[JSpecify](https://jspecify.dev/) provides standardized `@Nullable`/`@NonNull` annotations, and [NullAway](https://github.com/uber/NullAway) (by Uber) is an Error Prone plugin that enforces them at compile time. Spring Boot 4.0 / Spring Framework 7.0 have adopted JSpecify natively, making this the recommended approach for Java null safety.

With JSpecify + NullAway configured, Java gets compile-time null checking that catches most NPEs before runtime. It's not type-system-level like Kotlin (you can still bypass it), but it significantly narrows the gap for teams willing to adopt the tooling.

## Source Files

- **Kotlin**: `kotlin-app/src/main/kotlin/com/showcase/kotlin/slice/nullsafety/`
  - `NullSafeWorkflow.kt` — `?.`, `?:`, `let`, `also` for null handling
- **Java**: `java-app/src/main/java/com/showcase/java/slice/nullsafety/`
  - `NullSafeWorkflow.java` — `Optional`, `@Nullable`, null-pattern `switch`
