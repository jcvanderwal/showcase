package com.showcase.java.slice.nullsafety;

import com.showcase.java.domain.event.DomainEvent;
import com.showcase.java.domain.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demonstrates Java 25's null handling patterns in an event-sourced task workflow.
 *
 * <p>Key Java 25 features showcased:
 * <ul>
 *   <li>{@link Optional} for explicit nullable return types</li>
 *   <li>{@code @Nullable} annotations (documentation-level, not compiler-enforced)</li>
 *   <li>Null-pattern in switch expressions ({@code case null ->})</li>
 *   <li>Pattern matching for null checks</li>
 * </ul>
 *
 * <p>Comparison with Kotlin: Java 25 relies on runtime checks and conventions.
 * {@code @Nullable} annotations are advisory — the compiler does not reject null
 * assignments to non-annotated parameters. {@link Optional} adds ceremony but
 * provides explicit intent. The null-pattern in switch is a nice ergonomic
 * improvement but does not provide compile-time guarantees.
 */
public class NullSafeWorkflow {

    private final Map<String, TaskSummary> tasks = new ConcurrentHashMap<>();

    // -----------------------------------------------------------------------
    // OPTIONAL FOR NULLABLE RETURNS
    // -----------------------------------------------------------------------

    /**
     * Looks up a task and returns its assignee name using Optional chains.
     *
     * <p>Java uses {@link Optional} to signal that a value may be absent.
     * The chain {@code .map().map()} mirrors Kotlin's safe call chain {@code ?.}.
     */
    public Optional<String> findAssigneeName(String taskId) {
        return Optional.ofNullable(tasks.get(taskId))
                .map(TaskSummary::assignee)
                .map(UserId::value);
    }

    /**
     * Returns the due date of a task, or empty if the task doesn't exist or has no due date.
     */
    public Optional<LocalDate> findDueDate(String taskId) {
        return Optional.ofNullable(tasks.get(taskId))
                .map(TaskSummary::dueDate);
    }

    // -----------------------------------------------------------------------
    // DEFAULT VALUES (orElse — equivalent to Kotlin's elvis ?:)
    // -----------------------------------------------------------------------

    /**
     * Returns the assignee name or a default placeholder.
     *
     * <p>{@code orElse} is Java's equivalent of Kotlin's elvis operator {@code ?:}.
     */
    public String getAssigneeOrDefault(String taskId) {
        return Optional.ofNullable(tasks.get(taskId))
                .map(TaskSummary::assignee)
                .map(UserId::value)
                .orElse("unassigned");
    }

    /**
     * Returns the task title or throws if the task doesn't exist.
     *
     * <p>{@code orElseThrow} is Java's equivalent of Kotlin's {@code ?: throw}.
     */
    public String getTaskTitleOrThrow(String taskId) {
        return Optional.ofNullable(tasks.get(taskId))
                .map(TaskSummary::title)
                .orElseThrow(() -> new NoSuchElementException("Task " + taskId + " not found"));
    }

    // -----------------------------------------------------------------------
    // NULL-PATTERN SWITCH (Java 25)
    // -----------------------------------------------------------------------

    /**
     * Demonstrates Java 25's null-pattern in switch for handling nullable task lookups.
     *
     * <p>The {@code case null} arm handles the null case explicitly in the switch,
     * avoiding a separate null check before the switch.
     */
    public String describeTask(String taskId) {
        var task = tasks.get(taskId);
        return switch (task) {
            case null -> "Task " + taskId + " not found";
            case TaskSummary t -> "Task: " + t.title() + " [" + describeStatus(t.status()) + "]";
        };
    }

    /**
     * Describes a task status using exhaustive switch with null-pattern support.
     */
    public String describeStatus(TaskStatus status) {
        return switch (status) {
            case null -> "unknown";
            case TaskStatus.Open _ -> "open";
            case TaskStatus.InProgress s -> "in progress, assigned to " + s.assignee().value();
            case TaskStatus.Blocked s -> "blocked: " + s.reason();
            case TaskStatus.Completed s -> "completed at " + s.completedAt();
        };
    }

    // -----------------------------------------------------------------------
    // ifPresent — equivalent to Kotlin's ?.let { }
    // -----------------------------------------------------------------------

    /**
     * Processes a task assignment using Optional.ifPresent for conditional execution.
     *
     * <p>This mirrors Kotlin's {@code ?.let { ... }} pattern.
     */
    public Optional<TaskSummary> processAssignment(String taskId, UserId assigneeId) {
        var existing = tasks.get(taskId);
        if (existing == null) {
            return Optional.empty();
        }
        var updated = new TaskSummary(
                existing.id(),
                existing.title(),
                new TaskStatus.InProgress(assigneeId),
                existing.priority(),
                assigneeId,
                existing.dueDate(),
                existing.tags()
        );
        tasks.put(taskId, updated);
        return Optional.of(updated);
    }

    // -----------------------------------------------------------------------
    // EVENT REPLAY WITH NULL HANDLING
    // -----------------------------------------------------------------------

    /**
     * Replays events to build task state, demonstrating Java null handling patterns.
     *
     * <p>Java has no compile-time null safety — nullable fields like {@code description}
     * and {@code dueDate} in records are just regular references that may be null.
     * The programmer must remember to check; the compiler won't help.
     */
    public void replayEvents(List<DomainEvent> events) {
        for (var event : events) {
            switch (event) {
                case DomainEvent.TaskCreated e -> tasks.put(e.aggregateId(), new TaskSummary(
                        e.aggregateId(),
                        e.title(),
                        new TaskStatus.Open(),
                        e.priority(),
                        null,        // no assignee yet — null, not Optional
                        e.dueDate(), // may be null — no compile-time warning
                        e.tags()
                ));

                case DomainEvent.TaskAssigned e -> {
                    var existing = tasks.get(e.aggregateId());
                    if (existing != null) {
                        tasks.put(e.aggregateId(), new TaskSummary(
                                existing.id(),
                                existing.title(),
                                new TaskStatus.InProgress(e.assigneeId()),
                                existing.priority(),
                                e.assigneeId(),
                                existing.dueDate(),
                                existing.tags()
                        ));
                    }
                }

                case DomainEvent.TaskCompleted e -> {
                    var existing = tasks.get(e.aggregateId());
                    if (existing != null) {
                        tasks.put(e.aggregateId(), existing.withStatus(
                                new TaskStatus.Completed(e.completedAt())
                        ));
                    }
                }

                case DomainEvent.TaskPriorityChanged e -> {
                    var existing = tasks.get(e.aggregateId());
                    if (existing != null) {
                        tasks.put(e.aggregateId(), existing.withPriority(e.newPriority()));
                    }
                }

                case DomainEvent.TagAddedToTask e -> {
                    var existing = tasks.get(e.aggregateId());
                    if (existing != null) {
                        var newTags = new HashSet<>(existing.tags());
                        newTags.add(e.tag());
                        tasks.put(e.aggregateId(), existing.withTags(Set.copyOf(newTags)));
                    }
                }

                case DomainEvent.TaskMovedToProject _ -> {
                    // No status change
                }
            }
        }
    }

    /**
     * Returns all tasks currently tracked.
     */
    public List<TaskSummary> getAllTasks() {
        return List.copyOf(tasks.values());
    }
}
