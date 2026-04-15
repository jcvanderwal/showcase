package com.showcase.java.slice.utilitymethods;

import com.showcase.java.domain.model.TaskStatus;
import com.showcase.java.domain.model.TaskSummary;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Slice 4 — Utility Methods (Req 5)
 *
 * Java 25 equivalent of Kotlin extension functions on TaskSummary.
 * Since Java has no extension methods, these are static utility methods.
 * Callers must know this class exists — there is no IDE auto-complete
 * on the TaskSummary type itself.
 */
public final class TaskUtils {

    private TaskUtils() {}

    /**
     * Returns true if the task is overdue: has a due date in the past
     * and is not yet completed.
     */
    public static boolean isOverdue(TaskSummary task) {
        var due = task.dueDate();
        if (due == null) return false;
        return due.isBefore(LocalDate.now())
                && !(task.status() instanceof TaskStatus.Completed);
    }

    /**
     * Returns the number of days until the due date, or null if no due date is set.
     * Negative values indicate the task is past due.
     */
    public static Long daysUntilDue(TaskSummary task) {
        var due = task.dueDate();
        return due != null ? ChronoUnit.DAYS.between(LocalDate.now(), due) : null;
    }

    /**
     * Groups a list of task summaries by their status class name.
     */
    public static Map<String, List<TaskSummary>> groupByStatus(List<TaskSummary> tasks) {
        return tasks.stream()
                .collect(Collectors.groupingBy(t -> t.status().getClass().getSimpleName()));
    }

    /**
     * Converts a string to a URL-friendly slug.
     */
    public static String toSlug(String input) {
        return input.trim()
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("-$", "");
    }
}
