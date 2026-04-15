# Type-Safe DSL Builders for Aggregates and Projects

**Convergence Matrix Tier: Kotlin still leads**

## Overview

This slice compares Kotlin's DSL capabilities (lambdas with receivers, `@DslMarker`) against Java 25's fluent builder pattern for constructing project configurations and aggregate behavior definitions.

## Kotlin Approach

Kotlin's lambdas with receivers enable type-safe, declarative DSLs with scope control via `@DslMarker`:

```kotlin
val project = project {
    name("Q4 Release")
    task {
        title("Implement feature X")
        priority(Priority.HIGH)
        dueDate(LocalDate.of(2025, 3, 1))
    }
    task {
        title("Write tests")
        priority(Priority.MEDIUM)
    }
}

val behavior = aggregateBehavior {
    onCommand<Command.CreateTask> { cmd -> listOf(TaskCreated(...)) }
    onCommand<Command.AssignTask> { cmd -> listOf(TaskAssigned(...)) }
}
```

## Java 25 Approach

Java 25 uses fluent builder chains with method chaining:

```java
var project = ProjectBuilder.create()
    .name("Q4 Release")
    .task(t -> t
        .title("Implement feature X")
        .priority(Priority.HIGH)
        .dueDate(LocalDate.of(2025, 3, 1)))
    .task(t -> t
        .title("Write tests")
        .priority(Priority.MEDIUM))
    .build();
```

## Honest Comparison

| Aspect | Kotlin DSL | Java 25 Builder |
|--------|-----------|-----------------|
| Syntax style | Declarative, nested blocks | Fluent method chains |
| Scope control | `@DslMarker` prevents leaking | Manual (no equivalent) |
| Compile-time constraints | Receiver type restricts available methods | Builder state pattern (verbose) |
| Nesting depth | Natural with lambdas | Lambda parameters for nesting |
| Readability | Reads like a configuration file | Reads like method chains |

Java 25 records and sealed interfaces have reduced boilerplate for simple builders, and lambda parameters enable nesting. However, Kotlin's lambdas-with-receivers provide a fundamentally different capability: the receiver type controls what methods are available inside each block, and `@DslMarker` prevents accidental scope leaking. Java 25 has no equivalent mechanism.

## Java Ecosystem Workaround: Jilt (Partial)

[Jilt](https://github.com/skinny85/jilt) is an annotation processor that auto-generates type-safe staged builders, enforcing required fields at compile time via the type system (each setter returns a different interface, so you can't call `build()` until all required fields are set). This addresses the "required fields" aspect of Kotlin DSLs but does not replicate `@DslMarker` scope control or the nested-block syntax that lambdas-with-receivers provide. This remains the widest gap in the DSL space.

## Source Files

- **Kotlin**: `kotlin-app/src/main/kotlin/com/showcase/kotlin/slice/dslbuilders/`
  - `ProjectDsl.kt` — type-safe project configuration DSL
  - `AggregateBehaviorDsl.kt` — declarative aggregate behavior DSL
- **Java**: `java-app/src/main/java/com/showcase/java/slice/builders/`
  - `ProjectBuilder.java` — fluent builder for project configuration
  - `AggregateBehaviorBuilder.java` — fluent builder for aggregate behavior
