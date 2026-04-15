package com.showcase.java.slice.builders;

import com.showcase.java.domain.model.Priority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Slice 5 — Fluent Builders (Req 6)
 *
 * Java 25 equivalent of the Kotlin ProjectDsl. Uses a fluent builder pattern
 * with records for the immutable result types. Functional but more verbose
 * than Kotlin's lambdas-with-receivers approach.
 */
public final class ProjectBuilder {

    /** Immutable project configuration result. */
    public record ProjectConfig(String name, String description, List<TaskConfig> tasks) {
        public ProjectConfig {
            tasks = List.copyOf(tasks);
        }
    }

    /** Immutable task configuration result. */
    public record TaskConfig(String title, Priority priority, LocalDate dueDate, Set<String> tags) {
        public TaskConfig {
            tags = Set.copyOf(tags);
        }
    }

    private String name;
    private String description;
    private final List<TaskConfig> tasks = new ArrayList<>();

    public ProjectBuilder name(String name) {
        this.name = name;
        return this;
    }

    public ProjectBuilder description(String description) {
        this.description = description;
        return this;
    }

    public ProjectBuilder task(TaskConfigBuilder taskBuilder) {
        tasks.add(taskBuilder.build());
        return this;
    }

    public ProjectConfig build() {
        if (name == null || name.isBlank()) {
            throw new IllegalStateException("Project name is required");
        }
        return new ProjectConfig(name, description, tasks);
    }

    /** Nested fluent builder for task configurations. */
    public static final class TaskConfigBuilder {
        private String title;
        private Priority priority = Priority.MEDIUM;
        private LocalDate dueDate;
        private final Set<String> tags = new LinkedHashSet<>();

        public TaskConfigBuilder title(String title) {
            this.title = title;
            return this;
        }

        public TaskConfigBuilder priority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public TaskConfigBuilder dueDate(LocalDate dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public TaskConfigBuilder tag(String tag) {
            tags.add(tag);
            return this;
        }

        public TaskConfig build() {
            if (title == null || title.isBlank()) {
                throw new IllegalStateException("Task title is required");
            }
            return new TaskConfig(title, priority, dueDate, tags);
        }
    }
}
