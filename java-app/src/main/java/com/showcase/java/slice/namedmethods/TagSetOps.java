package com.showcase.java.slice.namedmethods;

import com.showcase.java.domain.model.TaskSummary;

import java.util.HashSet;
import java.util.Set;

/**
 * Slice 7 — Named Methods: TagSetOps
 *
 * Java equivalent of Kotlin's operator overloading for tag set merging.
 * Since Java has no operator overloading, we use explicit named methods
 * like {@code mergeTags()} instead of the {@code +} operator.
 */
public final class TagSetOps {

    private TagSetOps() {}

    /**
     * Merges tag sets from two tasks.
     * Kotlin equivalent: task1 + task2
     */
    public static Set<String> mergeTags(TaskSummary task1, TaskSummary task2) {
        var merged = new HashSet<>(task1.tags());
        merged.addAll(task2.tags());
        return Set.copyOf(merged);
    }

    /**
     * Adds a single tag to a task's tag set.
     * Kotlin equivalent: task + "urgent"
     */
    public static Set<String> addTag(TaskSummary task, String tag) {
        var merged = new HashSet<>(task.tags());
        merged.add(tag);
        return Set.copyOf(merged);
    }

    /**
     * Merges an additional tag set into a task's tags.
     * Kotlin equivalent: task + setOf("backend", "api")
     */
    public static Set<String> addTags(TaskSummary task, Set<String> tags) {
        var merged = new HashSet<>(task.tags());
        merged.addAll(tags);
        return Set.copyOf(merged);
    }
}
