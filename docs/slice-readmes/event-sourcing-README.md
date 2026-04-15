# Why Event Sourcing?

## Overview

This document explains why the Task Manager domain uses event sourcing and CQRS as its architectural foundation. The choice is intentional: event sourcing naturally exercises the language features being compared across all 10 feature slices.

## How Event Sourcing Exercises Language Features

Event sourcing isn't just an architecture choice — it's a feature showcase amplifier. Each core ES/CQRS concept maps directly to a language capability being compared:

| ES/CQRS Concept | Language Feature Exercised |
|-----------------|---------------------------|
| **Commands** (sealed hierarchy) | Sealed classes, exhaustive pattern matching |
| **Domain Events** (immutable facts) | Data classes / records, `copy()` for modification |
| **Command dispatch** (exhaustive handling) | `when` expressions / `switch` pattern matching |
| **Event processing** (async fan-out) | Coroutines / virtual threads |
| **Projections** (read model building) | Scope functions, builder patterns |
| **Event Store** (interface + decorator) | Delegation (`by` keyword vs manual) |
| **Event filtering** (by type) | Reified generics / class tokens |
| **Tag operations** (set merging) | Operator overloading / named methods |
| **Aggregate configuration** (declarative) | DSL builders / fluent builders |
| **Nullable fields** (dueDate, assignee) | Null safety (`?.` `?:` vs `Optional`) |

## Why Not a Simpler Architecture?

A basic CRUD application would exercise only a fraction of these features. Event sourcing provides:

1. **Rich type hierarchies** — Commands and events form natural sealed hierarchies with data-carrying variants, exercising pattern matching in both languages.

2. **Async processing** — Event distribution to multiple projections is inherently concurrent, providing a realistic use case for coroutines and virtual threads.

3. **Decorator patterns** — The EventStore interface invites delegation (logging, auditing), showcasing Kotlin's `by` keyword vs Java's manual forwarding.

4. **Immutable data transformation** — Events are immutable facts that get transformed, filtered, and projected — ideal for comparing `copy()`, extension functions, and scope functions.

5. **Type-safe identifiers** — Aggregate IDs, task IDs, and project IDs benefit from value classes / records, demonstrating zero-overhead type safety.

## Domain Model

Both apps implement the same Task Manager domain:

- **TaskAggregate** — manages task lifecycle (create, assign, complete, tag, move)
- **ProjectAggregate** — manages project membership
- **Commands** — 6 command types (CreateTask, AssignTask, ChangeTaskPriority, CompleteTask, AddTagToTask, MoveTaskToProject)
- **Events** — 6 event types mirroring the commands
- **Projections** — TaskSummaryProjection (current state), OverdueTasksProjection (filtered view)
- **EventStore** — append-only, in-memory, `ConcurrentHashMap`-backed
- **EventBus** — distributes events to subscribers

## Source Files

- **Kotlin domain**: `kotlin-app/src/main/kotlin/com/showcase/kotlin/domain/`
- **Java domain**: `java-app/src/main/java/com/showcase/java/domain/`
