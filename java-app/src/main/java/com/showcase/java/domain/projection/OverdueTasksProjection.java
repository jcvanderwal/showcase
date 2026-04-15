package com.showcase.java.domain.projection;

import com.showcase.java.domain.event.DomainEvent;
import com.showcase.java.domain.model.TaskStatus;
import com.showcase.java.domain.model.TaskSummary;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class OverdueTasksProjection implements Projection {

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
                // Project move doesn't affect overdue tracking
            }
        }
    }

    public List<TaskSummary> getOverdueTasks(LocalDate asOf) {
        return tasks.values().stream()
                .filter(s -> s.dueDate() != null
                        && s.dueDate().isBefore(asOf)
                        && !(s.status() instanceof TaskStatus.Completed))
                .toList();
    }

    public List<TaskSummary> getOverdueTasks() {
        return getOverdueTasks(LocalDate.now());
    }
}
