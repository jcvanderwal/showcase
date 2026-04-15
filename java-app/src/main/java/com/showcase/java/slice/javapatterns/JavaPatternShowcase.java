package com.showcase.java.slice.javapatterns;

import com.showcase.java.domain.command.Command;
import com.showcase.java.domain.command.CommandHandler;
import com.showcase.java.domain.event.DomainEvent;
import com.showcase.java.domain.model.*;
import com.showcase.java.domain.store.EventStore;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Slice 8 — Java Patterns: JavaPatternShowcase
 *
 * Java 25 equivalent of Kotlin's scope functions, copy(), destructuring,
 * and string templates. Uses records, record patterns, deconstruction,
 * and var to achieve similar results.
 *
 * Java 25 has narrowed the gap with record patterns and deconstruction,
 * but Kotlin's scope functions and copy() remain more concise.
 */
public final class JavaPatternShowcase {

    private final EventStore eventStore;
    private final CommandHandler handler;

    public JavaPatternShowcase(EventStore eventStore, CommandHandler handler) {
        this.eventStore = eventStore;
        this.handler = handler;
    }

    // ── Nullable handling (Java equivalent of Kotlin let) ─────────────
    /**
     * Java equivalent of Kotlin's `let` for nullable transformation.
     * Uses Optional-style null checks instead of ?.let { }.
     */
    public String formatAssignment(DomainEvent.TaskAssigned event) {
        if (event == null || event.assigneeId() == null) {
            return "No assignment";
        }
        return "Task %s assigned to %s".formatted(
                event.aggregateId(), event.assigneeId().value());
    }

    // ── Method chaining (Java equivalent of Kotlin run) ───────────────
    /**
     * Java equivalent of Kotlin's `run` — just a direct method call.
     * No scope function needed; Java uses straightforward method invocation.
     */
    public List<DomainEvent> createAndReturnEvents(
            TaskId taskId, String title, Priority priority) {
        return handler.handle(new Command.CreateTask(
                taskId, title, null, priority, null, Set.of()));
    }

    // ── Record deconstruction (Java equivalent of Kotlin with/destructuring) ──
    /**
     * Java equivalent of Kotlin's `with` and destructuring.
     * Uses record patterns for deconstruction in Java 25.
     */
    public String summarize(TaskSummary task) {
        return """
                Task: %s (%s)
                Status: %s
                Priority: %s
                Tags: %s
                Due: %s""".formatted(
                task.title(), task.id(),
                task.status(), task.priority(),
                String.join(", ", task.tags()),
                task.dueDate() != null ? task.dueDate().toString() : "No due date");
    }

    // ── Record construction (Java equivalent of Kotlin apply) ─────────
    /**
     * Java equivalent of Kotlin's `apply` for object initialization.
     * Java records are constructed directly — no apply scope needed.
     */
    public TaskSummary buildDefaultTask(String id, String title) {
        return new TaskSummary(
                id, title,
                new TaskStatus.Open(),
                Priority.LOW,
                null, null,
                Set.of("new"));
    }

    // ── Side effects (Java equivalent of Kotlin also) ─────────────────
    /**
     * Java equivalent of Kotlin's `also` for logging side effects.
     * Uses a standard for-each loop — no scope function available.
     */
    public List<DomainEvent> processEventsWithLogging(List<DomainEvent> events) {
        for (var event : events) {
            System.out.println("[LOG] Processing event: %s for aggregate %s"
                    .formatted(event.getClass().getSimpleName(), event.aggregateId()));
        }
        return events;
    }

    // ── Manual copy (Java equivalent of Kotlin data class copy()) ─────
    /**
     * Java records have no built-in copy(). You must manually construct
     * a new record with the modified fields.
     * Kotlin equivalent: original.copy(assigneeId = newAssignee)
     */
    public DomainEvent.TaskAssigned reassignEvent(
            DomainEvent.TaskAssigned original, UserId newAssignee) {
        return new DomainEvent.TaskAssigned(
                original.aggregateId(), Instant.now(), newAssignee);
    }

    public DomainEvent.TaskPriorityChanged reprioritizeEvent(
            DomainEvent.TaskPriorityChanged original, Priority newPriority) {
        return new DomainEvent.TaskPriorityChanged(
                original.aggregateId(), Instant.now(),
                original.oldPriority(), newPriority);
    }

    // ── Record pattern matching (Java equivalent of Kotlin destructuring) ──
    /**
     * Java 25 record patterns allow deconstruction in switch/instanceof.
     * Kotlin equivalent: val (aggregateId, timestamp, title, ...) = event
     */
    public String describeCreation(DomainEvent.TaskCreated event) {
        var result = "Created '%s' (id=%s) at %s with priority %s".formatted(
                event.title(), event.aggregateId(),
                event.timestamp(), event.priority());
        if (event.description() != null) {
            result += ", desc: " + event.description();
        }
        if (event.dueDate() != null) {
            result += ", due: " + event.dueDate();
        }
        if (event.tags() != null && !event.tags().isEmpty()) {
            result += ", tags: " + String.join(", ", event.tags());
        }
        return result;
    }

    // ── String formatting (Java equivalent of Kotlin string templates) ──
    /**
     * Java uses String.formatted() or switch expressions.
     * Kotlin equivalent: "📋 Task '${event.title}' created..."
     */
    public String logEvent(DomainEvent event) {
        return switch (event) {
            case DomainEvent.TaskCreated e ->
                    "📋 Task '%s' created with priority %s at %s"
                            .formatted(e.title(), e.priority(), e.timestamp());
            case DomainEvent.TaskAssigned e ->
                    "👤 Task %s assigned to %s at %s"
                            .formatted(e.aggregateId(), e.assigneeId().value(), e.timestamp());
            case DomainEvent.TaskPriorityChanged e ->
                    "⚡ Task %s priority changed: %s → %s"
                            .formatted(e.aggregateId(), e.oldPriority(), e.newPriority());
            case DomainEvent.TaskCompleted e ->
                    "✅ Task %s completed at %s"
                            .formatted(e.aggregateId(), e.completedAt());
            case DomainEvent.TagAddedToTask e ->
                    "🏷️ Tag '%s' added to task %s"
                            .formatted(e.tag(), e.aggregateId());
            case DomainEvent.TaskMovedToProject e ->
                    "📁 Task %s moved to project %s"
                            .formatted(e.aggregateId(), e.projectId().value());
        };
    }
}
