package com.showcase.java.slice.sealedclasses;

import com.showcase.java.domain.command.Command;
import com.showcase.java.domain.event.DomainEvent;
import com.showcase.java.domain.model.Priority;

import java.time.Instant;
import java.util.List;
import java.util.Set;

/**
 * Demonstrates Java 25's exhaustive switch with pattern matching for sealed interface dispatch.
 *
 * <p>Key Java 25 features showcased:
 * <ul>
 *   <li>Exhaustive switch expression over sealed interface {@link Command}</li>
 *   <li>Record pattern matching — each case binds a pattern variable</li>
 *   <li>No default branch needed — the compiler verifies exhaustiveness</li>
 *   <li>Adding a new Command record forces a compile error here</li>
 * </ul>
 *
 * <p>Comparison with Kotlin: Java 25 sealed interfaces + records + exhaustive switch
 * have reached near-parity with Kotlin sealed classes + when expressions. The main
 * differences are syntactic: Kotlin's smart casts avoid explicit pattern variable
 * binding, and Kotlin's when-as-expression is slightly more concise.
 */
public class JavaCommandHandler {

    /**
     * Dispatches a {@link Command} to produce {@link DomainEvent}s using an exhaustive switch.
     *
     * <p>The switch is exhaustive because {@link Command} is a sealed interface — the compiler
     * verifies every permitted record is handled. No default branch is needed.
     */
    public List<DomainEvent> handle(Command command) {
        return switch (command) {
            // Pattern variable `c` is bound to the matched CreateTask record.
            // All record components are accessible via accessor methods.
            case Command.CreateTask c -> {
                var now = Instant.now();
                yield List.of(new DomainEvent.TaskCreated(
                        c.taskId().value(),
                        now,
                        c.title(),
                        c.description(),
                        c.priority(),
                        c.dueDate(),
                        c.tags() != null ? c.tags() : Set.of()
                ));
            }

            // Pattern variable `c` bound to AssignTask — assigneeId() accessible
            case Command.AssignTask c -> List.of(new DomainEvent.TaskAssigned(
                    c.taskId().value(),
                    Instant.now(),
                    c.assigneeId()
            ));

            // Pattern variable `c` bound to ChangeTaskPriority — newPriority() accessible
            case Command.ChangeTaskPriority c -> List.of(new DomainEvent.TaskPriorityChanged(
                    c.taskId().value(),
                    Instant.now(),
                    Priority.MEDIUM, // simplified — real impl loads current
                    c.newPriority()
            ));

            // Pattern variable `c` bound to CompleteTask
            case Command.CompleteTask c -> {
                var now = Instant.now();
                yield List.of(new DomainEvent.TaskCompleted(
                        c.taskId().value(),
                        now,
                        now
                ));
            }

            // Pattern variable `c` bound to AddTagToTask — tag() accessible
            case Command.AddTagToTask c -> List.of(new DomainEvent.TagAddedToTask(
                    c.taskId().value(),
                    Instant.now(),
                    c.tag()
            ));

            // Pattern variable `c` bound to MoveTaskToProject — projectId() accessible
            case Command.MoveTaskToProject c -> List.of(new DomainEvent.TaskMovedToProject(
                    c.taskId().value(),
                    Instant.now(),
                    c.projectId()
            ));

            // No default — compiler enforces exhaustiveness over the sealed interface.
        };
    }
}
