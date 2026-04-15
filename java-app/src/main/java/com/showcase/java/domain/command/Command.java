package com.showcase.java.domain.command;

import com.showcase.java.domain.model.Priority;
import com.showcase.java.domain.model.ProjectId;
import com.showcase.java.domain.model.TaskId;
import com.showcase.java.domain.model.UserId;

import java.time.LocalDate;
import java.util.Set;

public sealed interface Command {

    String aggregateId();

    record CreateTask(
            TaskId taskId,
            String title,
            String description,
            Priority priority,
            LocalDate dueDate,
            Set<String> tags
    ) implements Command {
        @Override
        public String aggregateId() { return taskId.value(); }
    }

    record AssignTask(TaskId taskId, UserId assigneeId) implements Command {
        @Override
        public String aggregateId() { return taskId.value(); }
    }

    record ChangeTaskPriority(TaskId taskId, Priority newPriority) implements Command {
        @Override
        public String aggregateId() { return taskId.value(); }
    }

    record CompleteTask(TaskId taskId) implements Command {
        @Override
        public String aggregateId() { return taskId.value(); }
    }

    record AddTagToTask(TaskId taskId, String tag) implements Command {
        @Override
        public String aggregateId() { return taskId.value(); }
    }

    record MoveTaskToProject(TaskId taskId, ProjectId projectId) implements Command {
        @Override
        public String aggregateId() { return taskId.value(); }
    }
}
