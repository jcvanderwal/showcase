package com.showcase.java.slice.sealedclasses;

import com.showcase.java.domain.event.DomainEvent;
import com.showcase.java.domain.model.TaskStatus;

import java.util.List;

/**
 * Demonstrates Java 25's exhaustive switch with pattern matching for applying domain events
 * to produce task status transitions.
 *
 * <p>Key Java 25 features showcased:
 * <ul>
 *   <li>Exhaustive switch over sealed interface {@link DomainEvent}</li>
 *   <li>Exhaustive switch over sealed interface {@link TaskStatus}</li>
 *   <li>Record pattern matching with pattern variables</li>
 *   <li>Switch expressions returning values</li>
 *   <li>Compile-time safety: new event or status variants force updates here</li>
 * </ul>
 */
public class JavaEventApplier {

    /**
     * Applies a {@link DomainEvent} to the current {@link TaskStatus} and returns the new status.
     *
     * <p>Uses an exhaustive switch over the sealed {@link DomainEvent} hierarchy.
     * Each case binds a pattern variable for access to record components.
     */
    public TaskStatus apply(DomainEvent event, TaskStatus currentStatus) {
        return switch (event) {
            // TaskCreated always produces Open status
            case DomainEvent.TaskCreated _ -> new TaskStatus.Open();

            // TaskAssigned transitions to InProgress with the assignee
            case DomainEvent.TaskAssigned e -> new TaskStatus.InProgress(e.assigneeId());

            // TaskCompleted transitions to Completed with the timestamp
            case DomainEvent.TaskCompleted e -> new TaskStatus.Completed(e.completedAt());

            // Priority changes, tag additions, and project moves don't affect status
            case DomainEvent.TaskPriorityChanged _ -> currentStatus;
            case DomainEvent.TagAddedToTask _ -> currentStatus;
            case DomainEvent.TaskMovedToProject _ -> currentStatus;

            // No default — compiler enforces exhaustiveness over the sealed interface.
        };
    }

    /**
     * Demonstrates exhaustive switch over the sealed {@link TaskStatus} hierarchy.
     *
     * <p>Returns a human-readable description of the current status.
     */
    public String describeStatus(TaskStatus status) {
        return switch (status) {
            // Record with no components
            case TaskStatus.Open _ -> "Task is open and awaiting assignment";

            // Pattern variable gives access to assignee()
            case TaskStatus.InProgress s -> "Task is in progress, assigned to " + s.assignee().value();

            // Pattern variable gives access to reason()
            case TaskStatus.Blocked s -> "Task is blocked: " + s.reason();

            // Pattern variable gives access to completedAt()
            case TaskStatus.Completed s -> "Task was completed at " + s.completedAt();

            // No default — adding a new TaskStatus variant forces a compile error here.
        };
    }

    /**
     * Replays a sequence of events to derive the final {@link TaskStatus}.
     *
     * <p>Demonstrates fold-style reduction with exhaustive switch.
     */
    public TaskStatus replayEvents(List<DomainEvent> events) {
        TaskStatus status = new TaskStatus.Open();
        for (var event : events) {
            status = apply(event, status);
        }
        return status;
    }
}
