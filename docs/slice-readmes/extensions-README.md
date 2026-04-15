# Extension Functions and Properties

**Convergence Matrix Tier: Kotlin still leads**

## Overview

This slice compares Kotlin extension functions and properties against Java 25's static utility methods for enriching domain types in the Task Manager. Extensions let you add behavior to existing types without inheritance or wrappers.

## Kotlin Approach

Kotlin extension functions and properties appear as natural members of the extended type:

```kotlin
// Extension function — reads like a method on TaskSummary
fun TaskSummary.isOverdue(asOf: LocalDate = LocalDate.now()): Boolean =
    dueDate?.isBefore(asOf) == true && status !is TaskStatus.Completed

// Extension property
val TaskSummary.daysUntilDue: Long?
    get() = dueDate?.let { ChronoUnit.DAYS.between(LocalDate.now(), it) }

// Extension on collections
fun List<DomainEvent>.filterByTaskId(taskId: String) =
    filter { it.aggregateId == taskId }
```

## Java 25 Approach

Java 25 has no extension method mechanism. The equivalent is static utility methods:

```java
public class TaskUtils {
    public static boolean isOverdue(TaskSummary task, LocalDate asOf) {
        return task.dueDate() != null
            && task.dueDate().isBefore(asOf)
            && !(task.status() instanceof TaskStatus.Completed);
    }

    public static OptionalLong daysUntilDue(TaskSummary task) {
        return task.dueDate() == null ? OptionalLong.empty()
            : OptionalLong.of(ChronoUnit.DAYS.between(LocalDate.now(), task.dueDate()));
    }
}
```

## Honest Comparison

| Aspect | Kotlin | Java 25 |
|--------|--------|---------|
| Syntax | `task.isOverdue()` | `TaskUtils.isOverdue(task)` |
| Discoverability | IDE auto-complete on type | Must know utility class name |
| Extension properties | Supported | No equivalent |
| Chaining | Natural method chains | Nested static calls |
| Namespace pollution | Scoped by import | Class-level grouping |

Java 25 has no equivalent to extension functions. Static utility methods work but lack discoverability — you need to know `TaskUtils` exists. Kotlin extensions appear in IDE auto-complete on the type itself, making APIs more discoverable and code more readable.

## Source Files

- **Kotlin**: `kotlin-app/src/main/kotlin/com/showcase/kotlin/slice/extensions/`
  - `TaskExtensions.kt` — `isOverdue()`, `daysUntilDue`, `toSlug()`
  - `EventListExtensions.kt` — `filterByTaskId()`, `latestTimestamp`
- **Java**: `java-app/src/main/java/com/showcase/java/slice/utilitymethods/`
  - `TaskUtils.java` — static equivalents of task extensions
  - `EventListUtils.java` — static equivalents of event list extensions
