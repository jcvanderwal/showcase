package com.showcase.java.slice.virtualthreads;

import com.showcase.java.domain.event.DomainEvent;
import com.showcase.java.domain.projection.Projection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Demonstrates Java 25 virtual threads for concurrent event processing.
 *
 * <p>Key Java 25 features showcased:
 * <ul>
 *   <li>Virtual threads via {@code Executors.newVirtualThreadPerTaskExecutor()}</li>
 *   <li>Lightweight concurrency — millions of virtual threads possible</li>
 *   <li>Familiar {@link Future} API for result collection</li>
 * </ul>
 *
 * <p>Comparison with Kotlin: Virtual threads provide competitive concurrency for
 * straightforward fan-out patterns. However, they lack built-in structured
 * concurrency (parent-child relationships), cancellation propagation, and
 * reactive streaming (Flow) that Kotlin coroutines provide.
 *
 * <p>Requirements: 4.5
 */
public class VirtualThreadEventProcessor {

    private final List<Projection> projections;

    public VirtualThreadEventProcessor(List<Projection> projections) {
        this.projections = projections;
    }

    /**
     * Distributes events to all projections concurrently using virtual threads.
     *
     * <p>Each projection gets its own virtual thread. Virtual threads are cheap —
     * the JVM schedules them onto a small pool of platform threads.
     *
     * @throws RuntimeException wrapping the first projection failure
     */
    public void processEvents(List<DomainEvent> events) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<?>>();

            for (var projection : projections) {
                futures.add(executor.submit(() -> {
                    for (var event : events) {
                        projection.handle(event);
                    }
                }));
            }

            // Wait for all projections to complete
            for (var future : futures) {
                try {
                    future.get();
                } catch (ExecutionException e) {
                    throw new RuntimeException("Projection failed: " + e.getCause().getMessage(), e.getCause());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Processing interrupted", e);
                }
            }
        }
    }

    /**
     * Distributes events with partial failure tolerance.
     *
     * <p>Unlike Kotlin's {@code supervisorScope}, Java virtual threads don't have
     * built-in partial failure semantics. We implement it manually by catching
     * exceptions per-projection and collecting results.
     */
    public List<ProjectionResult> processEventsWithPartialFailureTolerance(List<DomainEvent> events) {
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            var futures = new ArrayList<Future<ProjectionResult>>();

            for (var projection : projections) {
                var name = projection.getClass().getSimpleName();
                futures.add(executor.submit(() -> {
                    try {
                        for (var event : events) {
                            projection.handle(event);
                        }
                        return new ProjectionResult.Success(name);
                    } catch (Exception e) {
                        return new ProjectionResult.Failure(name, e);
                    }
                }));
            }

            var results = new ArrayList<ProjectionResult>();
            for (var future : futures) {
                try {
                    results.add(future.get());
                } catch (ExecutionException | InterruptedException e) {
                    results.add(new ProjectionResult.Failure("unknown", e));
                }
            }
            return results;
        }
    }

    /**
     * Result of processing events through a single projection.
     */
    public sealed interface ProjectionResult {
        String projectionName();

        record Success(String projectionName) implements ProjectionResult {}
        record Failure(String projectionName, Exception error) implements ProjectionResult {}
    }
}
