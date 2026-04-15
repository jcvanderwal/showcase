# Multiplatform and Compilation Targets

**Convergence Matrix Tier: Kotlin still leads**

> This is a documentation-only slice. A full Kotlin Multiplatform setup is outside the scope of this showcase.

## Overview

This slice compares Kotlin Multiplatform's ability to share domain logic across JVM, JS, Native, and Wasm targets against Java 25's platform reach. The Kotlin app includes a minimal `expect`/`actual` example; the Java side is documentation only.

## Kotlin Multiplatform (KMP)

KMP allows sharing the Task Manager domain model — commands, events, aggregates, projections — across multiple platforms from a single codebase:

```kotlin
// Common code — shared across all targets
expect fun currentTimestamp(): Instant

// JVM actual
actual fun currentTimestamp(): Instant = Instant.now()

// JS actual
actual fun currentTimestamp(): Instant = Clock.System.now()

// Native actual
actual fun currentTimestamp(): Instant = Clock.System.now()
```

Shareable domain components:
- `Command` sealed hierarchy — identical on all platforms
- `DomainEvent` sealed hierarchy — identical on all platforms
- `EventStore` interface — platform-specific implementations
- `TaskSummaryProjection` — pure logic, fully shareable
- Business rules and validation — no platform dependencies

## Java 25 Platform Reach

Java 25 targets:
- **JVM** — primary and strongest target
- **Android** — via Android SDK (older Java versions, not Java 25)
- **GraalVM Native Image** — ahead-of-time compilation to native binaries
- **No browser/JS target** — requires separate frontend language
- **No iOS/native target** — no equivalent to Kotlin/Native

## Honest Comparison

| Aspect | Kotlin Multiplatform | Java 25 |
|--------|---------------------|---------|
| JVM | Full support | Full support |
| Android | Full support (native) | Partial (older Java versions) |
| Browser/JS | Kotlin/JS | Not supported |
| iOS/Native | Kotlin/Native | Not supported |
| WebAssembly | Kotlin/Wasm | Not supported |
| Shared domain logic | Single codebase, all targets | JVM only |
| `expect`/`actual` | Platform-specific implementations | N/A |

Kotlin Multiplatform is a genuine differentiator with no Java equivalent. The ability to share domain logic (commands, events, validation rules) across JVM, JS, Native, and Wasm from a single codebase is unique to Kotlin. Java 25 excels on the JVM but cannot share code with non-JVM platforms without rewriting.

## Source Files

- **Kotlin**: `kotlin-app/src/main/kotlin/com/showcase/kotlin/slice/multiplatform/`
  - `MultiplatformShowcase.kt` — minimal `expect`/`actual` example
- **Java**: No implementation — this is a documentation-only comparison
