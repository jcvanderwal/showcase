package com.showcase.java.slice.genericsrecords;

import com.showcase.java.domain.model.ProjectId;
import com.showcase.java.domain.model.TaskId;
import com.showcase.java.domain.model.UserId;

import java.util.List;

/**
 * Slice 9 — Generics & Records: TypeSafeIds
 *
 * Java equivalent of Kotlin value classes for type-safe identifiers.
 * Java records provide similar wrapper semantics and type safety,
 * but with runtime allocation overhead (records are heap-allocated objects).
 *
 * Kotlin value classes are erased at runtime — TaskId("task-1") becomes
 * just the String "task-1" on the JVM. Java records always allocate.
 */
public final class TypeSafeIds {

    private TypeSafeIds() {}

    /**
     * Demonstrates that records prevent mixing up identifier types.
     * Passing a UserId where a TaskId is expected is a compile-time error.
     *
     * // This would NOT compile:
     * // TaskId taskId = new UserId("user-1");  // Type mismatch!
     */
    public static String lookupTask(TaskId taskId) {
        return "Looking up task: " + taskId.value();
    }

    public static String lookupUser(UserId userId) {
        return "Looking up user: " + userId.value();
    }

    public static String lookupProject(ProjectId projectId) {
        return "Looking up project: " + projectId.value();
    }

    /**
     * Demonstrates that records work in collections with type safety.
     * Unlike Kotlin value classes, each record instance is heap-allocated.
     */
    public static List<TaskId> filterTaskIds(List<TaskId> ids, String prefix) {
        return ids.stream()
                .filter(id -> id.value().startsWith(prefix))
                .toList();
    }

    /**
     * Record for returning a triple of identifiers.
     * Kotlin equivalent uses Triple&lt;TaskId, ProjectId, UserId&gt;.
     */
    public record IdTriple(TaskId taskId, ProjectId projectId, UserId userId) {}

    /**
     * Demonstrates creating record-based identifiers.
     * Unlike Kotlin value classes, these are heap-allocated at runtime.
     */
    public static IdTriple createIds() {
        return new IdTriple(
                new TaskId("task-001"),
                new ProjectId("proj-001"),
                new UserId("user-001")
        );
    }
}
