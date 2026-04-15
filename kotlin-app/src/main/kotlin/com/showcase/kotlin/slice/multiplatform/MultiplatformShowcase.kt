package com.showcase.kotlin.slice.multiplatform

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

/**
 * Slice 10 — Multiplatform: Simulated expect/actual Example
 *
 * Since this is a JVM-only project, this file SIMULATES what Kotlin
 * Multiplatform (KMP) expect/actual declarations would look like for
 * event timestamp generation and formatting.
 *
 * In a real KMP project, the `expect` declarations would live in
 * `commonMain` and `actual` implementations in each platform source set
 * (jvmMain, jsMain, nativeMain, wasmMain).
 *
 * ═══════════════════════════════════════════════════════════════════
 * WHAT EXPECT/ACTUAL WOULD LOOK LIKE IN A REAL KMP PROJECT:
 * ═══════════════════════════════════════════════════════════════════
 *
 * // ── commonMain/kotlin/com/showcase/kotlin/platform/TimestampProvider.kt ──
 * //
 * // expect fun currentTimestamp(): Long
 * // expect fun formatTimestamp(epochMillis: Long): String
 * // expect class PlatformDateFormatter() {
 * //     fun format(epochMillis: Long, pattern: String): String
 * // }
 *
 * // ── jvmMain/kotlin/com/showcase/kotlin/platform/TimestampProvider.kt ──
 * //
 * // actual fun currentTimestamp(): Long = System.currentTimeMillis()
 * // actual fun formatTimestamp(epochMillis: Long): String =
 * //     Instant.ofEpochMilli(epochMillis)
 * //         .atZone(ZoneId.systemDefault())
 * //         .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
 * // actual class PlatformDateFormatter {
 * //     actual fun format(epochMillis: Long, pattern: String): String =
 * //         Instant.ofEpochMilli(epochMillis)
 * //             .atZone(ZoneId.systemDefault())
 * //             .format(DateTimeFormatter.ofPattern(pattern))
 * // }
 *
 * // ── jsMain/kotlin/com/showcase/kotlin/platform/TimestampProvider.kt ──
 * //
 * // actual fun currentTimestamp(): Long = js("Date.now()") as Long
 * // actual fun formatTimestamp(epochMillis: Long): String =
 * //     js("new Date(epochMillis).toISOString()") as String
 * // actual class PlatformDateFormatter {
 * //     actual fun format(epochMillis: Long, pattern: String): String =
 * //         js("new Date(epochMillis).toLocaleString()") as String
 * // }
 *
 * // ── nativeMain/kotlin/com/showcase/kotlin/platform/TimestampProvider.kt ──
 * //
 * // actual fun currentTimestamp(): Long =
 * //     platform.posix.time(null) * 1000L
 * // actual fun formatTimestamp(epochMillis: Long): String { ... }
 *
 * ═══════════════════════════════════════════════════════════════════
 * KMP CAPABILITIES SUMMARY:
 * ═══════════════════════════════════════════════════════════════════
 *
 * Kotlin Multiplatform allows sharing domain logic (Commands, Events,
 * Aggregates, Projections) across:
 *   - JVM (Android, server-side)
 *   - JavaScript (browser, Node.js)
 *   - Native (iOS, macOS, Linux, Windows)
 *   - Wasm (WebAssembly)
 *
 * The entire Task Manager domain — sealed class hierarchies for
 * Commands and Events, data classes for TaskSummary, value classes
 * for TaskId/ProjectId/UserId — could live in commonMain and be
 * shared across all platforms.
 *
 * Java 25 is limited to JVM and Android targets. It cannot share
 * code with iOS, browser, or native platforms without separate
 * toolchains (GraalVM Native Image for native, but no JS/Wasm).
 *
 * ═══════════════════════════════════════════════════════════════════
 */

// ── JVM actual implementation (what runs in this project) ─────────

/**
 * JVM implementation of timestamp generation.
 * In a KMP project, this would be the `actual` implementation in jvmMain.
 */
fun currentTimestamp(): Long = System.currentTimeMillis()

/**
 * JVM implementation of timestamp formatting.
 * In a KMP project, this would be the `actual` implementation in jvmMain.
 */
fun formatTimestamp(epochMillis: Long): String =
    Instant.ofEpochMilli(epochMillis)
        .atZone(ZoneId.systemDefault())
        .format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)

/**
 * JVM implementation of a platform-specific date formatter.
 * In a KMP project, this would be an `actual class` in jvmMain.
 */
class PlatformDateFormatter {
    fun format(epochMillis: Long, pattern: String): String =
        Instant.ofEpochMilli(epochMillis)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern(pattern))
}
