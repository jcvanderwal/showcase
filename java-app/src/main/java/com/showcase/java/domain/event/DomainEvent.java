package com.showcase.java.domain.event;

import com.showcase.java.domain.model.Priority;
import com.showcase.java.domain.model.ProjectId;
import com.showcase.java.domain.model.UserId;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;

public sealed interface DomainEvent {

    Instant timestamp();
    String aggregateId();

    record TaskCreated(
            String aggregateId,
            Instant timestamp,
            String title,
            String description,
            Priority priority,
            LocalDate dueDate,
            Set<String> tags
    ) implements DomainEvent {}

    record TaskAssigned(
            String aggregateId,
            Instant timestamp,
            UserId assigneeId
    ) implements DomainEvent {}

    record TaskPriorityChanged(
            String aggregateId,
            Instant timestamp,
            Priority oldPriority,
            Priority newPriority
    ) implements DomainEvent {}

    record TaskCompleted(
            String aggregateId,
            Instant timestamp,
            Instant completedAt
    ) implements DomainEvent {}

    record TagAddedToTask(
            String aggregateId,
            Instant timestamp,
            String tag
    ) implements DomainEvent {}

    record TaskMovedToProject(
            String aggregateId,
            Instant timestamp,
            ProjectId projectId
    ) implements DomainEvent {}
}
