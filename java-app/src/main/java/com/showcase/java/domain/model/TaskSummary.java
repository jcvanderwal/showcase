package com.showcase.java.domain.model;

import java.time.LocalDate;
import java.util.Set;

public record TaskSummary(
        String id,
        String title,
        TaskStatus status,
        Priority priority,
        UserId assignee,
        LocalDate dueDate,
        Set<String> tags
) {
    /** Returns a new TaskSummary with the given assignee. */
    public TaskSummary withAssignee(UserId assignee) {
        return new TaskSummary(id, title, status, priority, assignee, dueDate, tags);
    }

    /** Returns a new TaskSummary with the given status. */
    public TaskSummary withStatus(TaskStatus status) {
        return new TaskSummary(id, title, status, priority, assignee, dueDate, tags);
    }

    /** Returns a new TaskSummary with the given priority. */
    public TaskSummary withPriority(Priority priority) {
        return new TaskSummary(id, title, status, priority, assignee, dueDate, tags);
    }

    /** Returns a new TaskSummary with the given tags. */
    public TaskSummary withTags(Set<String> tags) {
        return new TaskSummary(id, title, status, priority, assignee, dueDate, tags);
    }
}
