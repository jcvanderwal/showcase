package com.showcase.java.domain.command;

import com.showcase.java.domain.bus.EventBus;
import com.showcase.java.domain.event.DomainEvent;
import com.showcase.java.domain.store.EventStore;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public class CommandHandler {

    private final EventStore eventStore;
    private final EventBus eventBus;

    public CommandHandler(EventStore eventStore, EventBus eventBus) {
        this.eventStore = eventStore;
        this.eventBus = eventBus;
    }

    public List<DomainEvent> handle(Command command) {
        var events = switch (command) {
            case Command.CreateTask c -> handleCreate(c);
            case Command.AssignTask c -> handleAssign(c);
            case Command.ChangeTaskPriority c -> handleChangePriority(c);
            case Command.CompleteTask c -> handleComplete(c);
            case Command.AddTagToTask c -> handleAddTag(c);
            case Command.MoveTaskToProject c -> handleMoveToProject(c);
        };
        eventStore.append(command.aggregateId(), events);
        eventBus.publish(events);
        return events;
    }

    private List<DomainEvent> handleCreate(Command.CreateTask cmd) {
        var now = Instant.now();
        return List.of(new DomainEvent.TaskCreated(
                cmd.taskId().value(),
                now,
                cmd.title(),
                cmd.description(),
                cmd.priority(),
                cmd.dueDate(),
                cmd.tags() != null ? cmd.tags() : Set.of()
        ));
    }

    private List<DomainEvent> handleAssign(Command.AssignTask cmd) {
        return List.of(new DomainEvent.TaskAssigned(
                cmd.taskId().value(),
                Instant.now(),
                cmd.assigneeId()
        ));
    }

    private List<DomainEvent> handleChangePriority(Command.ChangeTaskPriority cmd) {
        return List.of(new DomainEvent.TaskPriorityChanged(
                cmd.taskId().value(),
                Instant.now(),
                cmd.newPriority(), // simplified — real impl would load current priority
                cmd.newPriority()
        ));
    }

    private List<DomainEvent> handleComplete(Command.CompleteTask cmd) {
        var now = Instant.now();
        return List.of(new DomainEvent.TaskCompleted(
                cmd.taskId().value(),
                now,
                now
        ));
    }

    private List<DomainEvent> handleAddTag(Command.AddTagToTask cmd) {
        return List.of(new DomainEvent.TagAddedToTask(
                cmd.taskId().value(),
                Instant.now(),
                cmd.tag()
        ));
    }

    private List<DomainEvent> handleMoveToProject(Command.MoveTaskToProject cmd) {
        return List.of(new DomainEvent.TaskMovedToProject(
                cmd.taskId().value(),
                Instant.now(),
                cmd.projectId()
        ));
    }
}
