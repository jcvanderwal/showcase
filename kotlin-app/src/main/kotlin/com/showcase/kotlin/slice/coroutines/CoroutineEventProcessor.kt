package com.showcase.kotlin.slice.coroutines

import com.showcase.kotlin.domain.event.DomainEvent
import com.showcase.kotlin.domain.projection.Projection
import kotlinx.coroutines.*

/**
 * Demonstrates Kotlin coroutines for concurrent event processing with structured concurrency.
 *
 * Key Kotlin features showcased:
 * - `coroutineScope` + `async` for concurrent projection updates
 * - `supervisorScope` for partial failure tolerance
 * - Structured concurrency: child coroutines are scoped to the parent
 * - Suspend functions for non-blocking event distribution
 *
 * Requirements: 4.1, 4.2
 */
class CoroutineEventProcessor(
    private val projections: List<Projection>
) {

    /**
     * Distributes a list of domain events to all projections concurrently using `coroutineScope`.
     *
     * `coroutineScope` provides structured concurrency:
     * - All `async` coroutines are children of this scope
     * - If any projection throws, all sibling coroutines are cancelled
     * - The scope only completes when all children complete
     *
     * This is the "fail-fast" strategy: one failure cancels everything.
     */
    suspend fun processEvents(events: List<DomainEvent>) = coroutineScope {
        val deferreds = projections.map { projection ->
            async {
                for (event in events) {
                    projection.handle(event)
                }
            }
        }
        // Await all — if any throws, the coroutineScope cancels siblings
        deferreds.awaitAll()
    }

    /**
     * Distributes events to all projections concurrently using `supervisorScope`
     * for partial failure tolerance.
     *
     * `supervisorScope` differs from `coroutineScope`:
     * - If one child coroutine fails, siblings are NOT cancelled
     * - Each projection processes independently
     * - Failures are collected and reported, but successful projections still complete
     *
     * This is the "best-effort" strategy: one projection failing doesn't stop others.
     */
    suspend fun processEventsWithPartialFailureTolerance(
        events: List<DomainEvent>
    ): List<ProjectionResult> = supervisorScope {
        val deferreds = projections.map { projection ->
            async {
                try {
                    for (event in events) {
                        projection.handle(event)
                    }
                    ProjectionResult.Success(projection::class.simpleName ?: "unknown")
                } catch (e: Exception) {
                    // This projection failed, but others continue unaffected
                    ProjectionResult.Failure(
                        projectionName = projection::class.simpleName ?: "unknown",
                        error = e
                    )
                }
            }
        }
        deferreds.awaitAll()
    }

    /**
     * Processes a single event across all projections concurrently.
     *
     * Demonstrates `coroutineScope` + `async` for fan-out of a single event.
     */
    suspend fun processSingleEvent(event: DomainEvent) = coroutineScope {
        projections.map { projection ->
            async { projection.handle(event) }
        }.awaitAll()
    }
}

/**
 * Result of processing events through a single projection.
 */
sealed class ProjectionResult {
    abstract val projectionName: String

    data class Success(override val projectionName: String) : ProjectionResult()
    data class Failure(
        override val projectionName: String,
        val error: Exception
    ) : ProjectionResult()
}
