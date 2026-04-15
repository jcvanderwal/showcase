package com.showcase.java.domain.projection;

import com.showcase.java.domain.event.DomainEvent;
import com.showcase.java.domain.model.TaskStatus;
import com.showcase.java.domain.model.TaskSummary;

import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TaskSummaryProjection implements Projection {

    private final ConcurrentHashMap<String, TaskSummary> tasks = new ConcurrentHashMap<>();

    @Override
    public void handle(DomainEvent event) {
        switch (event) {
            case DomainEvent.TaskCreated e -> tasks.put(e.aggregateId(), new TaskSummary(
                    e.aggregateId(),
                    e.title(),
                    new TaskStatus.Open(),
                    e.priority(),
                    null,
                    e.dueDate(),
                    e.tags()
            ));
            case DomainEvent.TaskAssigned e -> tasks.computeIfPresent(e.aggregateId(),
                    (_, s) -> s.withAssignee(e.assigneeId())
                               .withStatus(new TaskStatus.InProgress(e.assigneeId())));
            case DomainEvent.TaskPriorityChanged e -> tasks.computeIfPresent(e.aggregateId(),
                    (_, s) -> s.withPriority(e.newPriority()));
            case DomainEvent.TaskCompleted e -> tasks.computeIfPresent(e.aggregateId(),
                    (_, s) -> s.withStatus(new TaskStatus.Completed(e.completedAt())));
            case DomainEvent.TagAddedToTask e -> tasks.computeIfPresent(e.aggregateId(),
                    (_, s) -> {
                        var newTags = new HashSet<>(s.tags());
                        newTags.add(e.tag());
                        return s.withTags(newTags);
                    });
            case DomainEvent.TaskMovedToProject _ -> {
                // Project move doesn't change task summary fields directly
            }
        }
    }

    public List<TaskSummary> getAll() {
        return List.copyOf(tasks.values());
    }

    public TaskSummary getById(String id) {
        return tasks.get(id);
    }

    public List<TaskSummary> getByStatus(TaskStatus status) {
        return tasks.values().stream()
                .filter(t -> t.status().equals(status))
                .toList();
    }
}
