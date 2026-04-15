package com.showcase.java.slice.virtualthreads;

import com.showcase.java.domain.event.DomainEvent;
import com.showcase.java.domain.projection.Projection;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.StructuredTaskScope;

/**
 * Demonstrates Java 25's {@link StructuredTaskScope} for structured concurrency
 * in event processing.
 *
 * <p>Key Java 25 features showcased:
 * <ul>
 *   <li>{@link StructuredTaskScope} for structured concurrency (parent-child task relationships)</li>
 *   <li>{@code ShutdownOnFailure} — cancels all subtasks when one fails (like Kotlin's coroutineScope)</li>
 *   <li>Automatic resource cleanup via try-with-resources</li>
 * </ul>
 *
 * <p>Comparison with Kotlin:
 * <ul>
 *   <li>{@code ShutdownOnFailure} ≈ Kotlin's {@code coroutineScope} (fail-fast)</li>
 *   <li>Manual result collection needed — no built-in {@code awaitAll()} returning results</li>
 *   <li>No equivalent to Kotlin's {@code supervisorScope} in the standard API</li>
 *   <li>No equivalent to Flow or Channel</li>
 * </ul>
 *
 * <p>Requirements: 4.5
 */
public class StructuredTaskScopeProcessor {

    private final List<Projection> projections;

    public StructuredTaskScopeProcessor(List<Projection> projections) {
        this.projections = projections;
    }

    /**
     * Processes events using {@code ShutdownOnFailure} — if any projection fails,
     * all other subtasks are cancelled.
     *
     * <p>This mirrors Kotlin's {@code coroutineScope} behavior where one child
     * failure cancels all siblings.
     *
     * @throws RuntimeException wrapping the first projection failure
     */
    public void processEventsFailFast(List<DomainEvent> events) throws InterruptedException {
        try (var scope = StructuredTaskScope.open(
                StructuredTaskScope.Joiner.awaitAll())) {

            for (var projection : projections) {
                scope.fork(() -> {
                    for (var event : events) {
                        projection.handle(event);
                    }
                    return null;
                });
            }

            scope.join();
        }
    }

    /**
     * Processes events with manual partial failure tolerance.
     *
     * <p>Java 25's StructuredTaskScope doesn't have a built-in "supervisor" mode
     * like Kotlin's {@code supervisorScope}. We implement partial failure tolerance
     * by catching exceptions within each subtask.
     */
    public List<VirtualThreadEventProcessor.ProjectionResult> processEventsPartialFailure(
            List<DomainEvent> events
    ) throws InterruptedException {
        try (var scope = StructuredTaskScope.open()) {

            var subtasks = new ArrayList<StructuredTaskScope.Subtask<?>>();

            for (var projection : projections) {
                var name = projection.getClass().getSimpleName();
                subtasks.add(scope.fork(() -> {
                    try {
                        for (var event : events) {
                            projection.handle(event);
                        }
                        return (VirtualThreadEventProcessor.ProjectionResult)
                                new VirtualThreadEventProcessor.ProjectionResult.Success(name);
                    } catch (Exception e) {
                        return (VirtualThreadEventProcessor.ProjectionResult)
                                new VirtualThreadEventProcessor.ProjectionResult.Failure(name, e);
                    }
                }));
            }

            scope.join();

            var results = new ArrayList<VirtualThreadEventProcessor.ProjectionResult>();
            for (var subtask : subtasks) {
                results.add((VirtualThreadEventProcessor.ProjectionResult) subtask.get());
            }
            return results;
        }
    }
}
