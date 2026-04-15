# Kotlin vs Java 25 Showcase

Two independent Spring Boot applications — one in **Kotlin 2.x**, one in **Java 25** — both implementing the same event-sourced Task Manager domain. Neither language is "the host." Both apps are fully idiomatic, first-class citizens with their own source trees, tests, and entry points.

The project provides an honest, side-by-side comparison of where Java 25 has caught up with Kotlin and where Kotlin still leads.

## Where Java 25 Has Caught Up

Java 25 has made significant progress. Before diving into Kotlin's remaining advantages, here's what's changed:

### At Parity: Sealed Classes + Pattern Matching

Java 25's sealed interfaces with records and exhaustive `switch` pattern matching now provide equivalent modeling power to Kotlin's sealed classes with `when`. Both languages offer closed type hierarchies, data-carrying variants, and compiler-enforced exhaustiveness. In our Task Manager domain, the `Command` and `DomainEvent` hierarchies look remarkably similar in both languages.

### Gap Narrowed: Virtual Threads + Structured Concurrency

Java 25 virtual threads with `StructuredTaskScope` handle the common "fan out to N projections" pattern well. For straightforward concurrent event processing, Java 25 is competitive. Kotlin still leads for reactive streams (Flow), channel-based communication, and suspend function composability.

### Gap Narrowed: Records + Deconstruction Patterns

Java 25 records with record patterns in `instanceof` and `switch` have significantly reduced the ceremony gap with Kotlin data classes. Record deconstruction approaches Kotlin's destructuring. Kotlin retains advantages in `copy()` for immutable modification and default parameter values.

## Where Kotlin Still Leads

Seven areas where Kotlin provides genuine advantages with no Java 25 equivalent:

| Feature | Why Kotlin Leads |
|---------|-----------------|
| **Null Safety** | Compile-time enforcement vs runtime-only checks |
| **Extension Functions** | Add methods to existing types; no Java equivalent |
| **DSL Builders** | Lambdas-with-receivers + `@DslMarker` for type-safe DSLs |
| **Delegation** | `by` keyword eliminates manual method forwarding |
| **Operator Overloading** | Domain-expressive operators; Java has none |
| **Reified Generics / Value Classes** | No type erasure for inline functions; zero-overhead wrappers |
| **Multiplatform** | Share domain logic across JVM, JS, Native, Wasm |

See the [Convergence Matrix](docs/CONVERGENCE_MATRIX.md) for detailed rationale and Java 25 feature citations.

## Project Structure

```
├── pom.xml                      # Parent POM (multi-module Maven)
├── kotlin-app/                  # Kotlin 2.x + Spring Boot app
│   ├── pom.xml
│   └── src/main/kotlin/com/showcase/kotlin/
│       ├── Application.kt       # Spring Boot entry point
│       ├── domain/              # Event-sourced domain core
│       └── slice/               # 10 feature slices
├── java-app/                    # Java 25 + Spring Boot app
│   ├── pom.xml
│   └── src/main/java/com/showcase/java/
│       ├── Application.java     # Spring Boot entry point
│       ├── domain/              # Event-sourced domain core
│       └── slice/               # 10 feature slices
└── docs/
    ├── CONVERGENCE_MATRIX.md    # Tier assessment for all 10 slices
    └── slice-readmes/           # Per-slice comparison READMEs
```

## Why Event Sourcing?

The event sourcing architecture was chosen because it naturally exercises the language features being compared: sealed hierarchies model commands and events, async primitives handle event processing, builder patterns define aggregate behavior, and extension/utility patterns enrich domain types. See [Event Sourcing README](docs/slice-readmes/event-sourcing-README.md) for details.

## Prerequisites

- **JDK 25** (required for both apps)
- **Maven 3.9+**
- **Kotlin 2.1+** (managed by Maven plugin)

## Build & Run

### Both apps together

```bash
mvn clean compile
```

### Kotlin app independently

```bash
cd kotlin-app
mvn clean compile
mvn spring-boot:run
```

### Java app independently

```bash
cd java-app
mvn clean compile
mvn spring-boot:run
```

### Run tests

```bash
# All tests
mvn clean test

# Kotlin tests only
mvn clean test -pl kotlin-app

# Java tests only
mvn clean test -pl java-app
```

## Feature Slices

Each slice implements the same functionality in both apps using idiomatic language patterns:

| # | Feature Area | Kotlin Slice | Java Slice | Parity | README |
|---|---|---|---|---|---|
| 1 | Sealed Classes | `sealedclasses/` | `sealedclasses/` | At parity | [README](docs/slice-readmes/sealed-classes-README.md) |
| 2 | Null Safety | `nullsafety/` | `nullsafety/` | Kotlin leads | [README](docs/slice-readmes/null-safety-README.md) |
| 3 | Coroutines / Virtual Threads | `coroutines/` | `virtualthreads/` | Gap narrowed | [README](docs/slice-readmes/coroutines-README.md) |
| 4 | Extensions / Utilities | `extensions/` | `utilitymethods/` | Kotlin leads | [README](docs/slice-readmes/extensions-README.md) |
| 5 | DSL Builders | `dslbuilders/` | `builders/` | Kotlin leads | [README](docs/slice-readmes/dsl-builders-README.md) |
| 6 | Delegation | `delegation/` | `delegation/` | Kotlin leads | [README](docs/slice-readmes/delegation-README.md) |
| 7 | Operators / Named Methods | `operators/` | `namedmethods/` | Kotlin leads | [README](docs/slice-readmes/operators-README.md) |
| 8 | Scope Functions / Patterns | `scopefunctions/` | `javapatterns/` | Gap narrowed | [README](docs/slice-readmes/scope-functions-README.md) |
| 9 | Inline / Reified / Value | `inlinereifiedvalue/` | `genericsrecords/` | Kotlin leads | [README](docs/slice-readmes/inline-reified-value-README.md) |
| 10 | Multiplatform | `multiplatform/` | docs only | Kotlin leads | [README](docs/slice-readmes/multiplatform-README.md) |

## Documentation

- [Convergence Matrix](docs/CONVERGENCE_MATRIX.md) — tier assessment with rationale for all 10 feature areas
- [Per-slice READMEs](docs/slice-readmes/) — detailed comparison for each feature slice
- [Event Sourcing Architecture](docs/slice-readmes/event-sourcing-README.md) — why this architecture was chosen

## Tech Stack

| Component | Kotlin App | Java App |
|-----------|-----------|----------|
| Language | Kotlin 2.1 | Java 25 |
| Framework | Spring Boot 4.0 | Spring Boot 4.0 |
| Async | Coroutines + Flow | Virtual Threads + StructuredTaskScope |
| Unit Tests | JUnit 5 | JUnit 5 |
| Property Tests | Kotest Property | jqwik |
| Build | Maven | Maven |
