# Kotlin vs Java 25 Convergence Matrix

This matrix categorizes each feature comparison area into one of three tiers based on how much Java 25 has closed the gap with Kotlin. The assessment is grounded in the event-sourced Task Manager domain implemented in both apps.

## Summary

| Tier | Count | Feature Areas |
|------|-------|---------------|
| Java 25 at parity | 1 | Sealed classes |
| Gap narrowed | 2 | Coroutines/virtual threads, Scope functions/patterns |
| Kotlin still leads | 7 | Null safety, Extensions, DSL builders, Delegation, Operators, Inline/reified/value, Multiplatform |

## Full Matrix

| Feature Area | Kotlin Approach | Java 25 Approach | Parity Level | Rationale |
|---|---|---|---|---|
| **Sealed Classes** | Sealed class hierarchy + exhaustive `when` | Sealed interfaces + records + exhaustive `switch` pattern matching | **Java 25 at parity** | Java 25 sealed interfaces with record patterns and exhaustive switch provide equivalent modeling power. Remaining Kotlin advantages (smart casts, expression-based `when`) are ergonomic, not capability gaps. |
| **Null Safety** | Compile-time null safety via `?`/`?.`/`?:`, scope functions | `Optional`, `@Nullable` annotations, null-pattern `switch` | **Kotlin still leads** | Java 25's null-pattern in switch improves ergonomics, but null safety remains runtime-only. Kotlin's type-system enforcement prevents entire categories of NPEs at compile time. |
| **Coroutines / Virtual Threads** | `coroutineScope`, `supervisorScope`, `Flow`, `Channel`, `suspend` | Virtual threads, `StructuredTaskScope`, `ShutdownOnFailure` | **Gap narrowed** | Java 25 virtual threads with `StructuredTaskScope` handle basic concurrent fan-out well. Kotlin still leads for reactive streams (Flow), channel-based communication, and suspend function composability. |
| **Extension Functions** | Extension functions and properties on domain types | Static utility methods, interface default methods | **Kotlin still leads** | Java 25 has no extension method mechanism. Static utilities lack discoverability and the natural call-site syntax that extensions provide. |
| **DSL Builders** | Lambdas with receivers, `@DslMarker` for scope control | Fluent builder pattern with method chaining | **Kotlin still leads** | Java 25 records reduce builder boilerplate, but lambdas-with-receivers and `@DslMarker` enable type-safe, scope-controlled DSLs that Java cannot replicate. |
| **Delegation** | `by` keyword for class delegation, property delegates (`lazy`, `observable`) | Manual method forwarding for every interface method | **Kotlin still leads** | Java 25 has no built-in delegation. Every interface method must be manually forwarded, increasing maintenance burden when interfaces evolve. |
| **Operator Overloading** | `plus`, `contains`, `compareTo`, `invoke` on domain types | Named methods (`mergeTags`, `contains`, `execute`) | **Kotlin still leads** | Java 25 has no operator overloading. Named methods are explicit but less expressive for domain operations like tag merging and event membership. |
| **Scope Functions / Patterns** | `let`/`run`/`with`/`apply`/`also`, `copy()`, destructuring, default params | Records, record patterns, deconstruction in `instanceof`/`switch`, `var` | **Gap narrowed** | Java 25 records with deconstruction patterns approach data class functionality. Kotlin retains advantages in `copy()`, `componentN()` flexibility, and default parameter values. |
| **Inline / Reified / Value** | Reified generics (no type erasure), value classes (zero overhead) | Class tokens for generics, records for type-safe wrappers (heap allocation) | **Kotlin still leads** | Type erasure is a fundamental JVM limitation. Kotlin's `reified` inline functions and value classes provide capabilities Java cannot match without language-level changes. |
| **Multiplatform** | KMP: share domain logic across JVM, JS, Native, Wasm | JVM only (GraalVM Native Image for native binaries) | **Kotlin still leads** | Kotlin Multiplatform has no Java equivalent. Sharing domain logic across JVM, browser, iOS, and Wasm from a single codebase is unique to Kotlin. |

## Tier Details

### Java 25 at Parity

**Sealed Classes** — Java 25's combination of sealed interfaces, records, and exhaustive switch pattern matching (JEP 441) has reached functional parity with Kotlin's sealed classes and `when` expressions. Both languages now provide:
- Closed type hierarchies with compiler-enforced exhaustiveness
- Data-carrying variants with automatic `equals`/`hashCode`/`toString`
- Pattern matching with type checking and deconstruction

The remaining Kotlin advantages are syntactic: smart casts within `when` branches, expression-based `when` returning values directly, and slightly more concise data class syntax.

### Gap Narrowed

**Coroutines / Virtual Threads** — Java 25 virtual threads (JEP 444) with `StructuredTaskScope` (JEP 480) provide a competitive model for structured concurrency. For the common pattern of "fan out to N projections and wait for all," both approaches work well. Kotlin's advantage lies in the richer primitives: `Flow` for reactive streams with backpressure, `Channel` for typed inter-component communication, and `suspend` functions that compose naturally.

**Scope Functions / Patterns** — Java 25 records (JEP 395) with record patterns (JEP 440) and unnamed variables (JEP 456) have significantly reduced the ceremony gap. Record deconstruction in `instanceof` and `switch` provides pattern matching capabilities that approach Kotlin's destructuring. The remaining Kotlin advantages — `copy()` for immutable modification, scope functions for fluent transformations, and default parameter values — are real but the gap has narrowed.

### Kotlin Still Leads

These seven areas represent genuine Kotlin advantages where Java 25 has no equivalent mechanism or only partial workarounds:

- **Null safety**: Compile-time vs runtime enforcement is a fundamental difference
- **Extension functions**: No Java equivalent for adding methods to existing types
- **DSL builders**: Lambdas-with-receivers and `@DslMarker` have no Java counterpart
- **Delegation**: `by` keyword eliminates boilerplate Java must write manually
- **Operator overloading**: Java has no operator overloading support
- **Reified generics / value classes**: Type erasure and heap allocation are JVM-level constraints
- **Multiplatform**: KMP's cross-platform code sharing is unique to Kotlin

## Per-Slice Documentation

Each feature area has a detailed README in [`docs/slice-readmes/`](slice-readmes/):

- [Sealed Classes](slice-readmes/sealed-classes-README.md)
- [Null Safety](slice-readmes/null-safety-README.md)
- [Coroutines vs Virtual Threads](slice-readmes/coroutines-README.md)
- [Extension Functions](slice-readmes/extensions-README.md)
- [DSL Builders](slice-readmes/dsl-builders-README.md)
- [Delegation](slice-readmes/delegation-README.md)
- [Operator Overloading](slice-readmes/operators-README.md)
- [Scope Functions / Patterns](slice-readmes/scope-functions-README.md)
- [Inline / Reified / Value](slice-readmes/inline-reified-value-README.md)
- [Multiplatform](slice-readmes/multiplatform-README.md)
- [Event Sourcing Architecture](slice-readmes/event-sourcing-README.md)
