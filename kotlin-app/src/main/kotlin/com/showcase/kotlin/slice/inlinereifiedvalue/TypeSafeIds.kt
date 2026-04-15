package com.showcase.kotlin.slice.inlinereifiedvalue

import com.showcase.kotlin.domain.model.ProjectId
import com.showcase.kotlin.domain.model.TaskId
import com.showcase.kotlin.domain.model.UserId

/**
 * Slice 9 — Value Classes: TypeSafeIds
 *
 * Demonstrates Kotlin value classes for type-safe identifiers with
 * zero runtime overhead. The value classes are defined in the domain
 * model (TaskId, ProjectId, UserId) — this file shows usage patterns
 * that highlight the compile-time safety they provide.
 *
 * Java records provide similar wrapper semantics but with runtime
 * allocation overhead. Kotlin value classes are erased at runtime.
 */
object TypeSafeIds {

    /**
     * Demonstrates that value classes prevent mixing up identifier types.
     * Passing a UserId where a TaskId is expected is a compile-time error.
     *
     * // This would NOT compile:
     * // val taskId: TaskId = UserId("user-1")  // Type mismatch!
     */
    fun lookupTask(taskId: TaskId): String =
        "Looking up task: ${taskId.value}"

    fun lookupUser(userId: UserId): String =
        "Looking up user: ${userId.value}"

    fun lookupProject(projectId: ProjectId): String =
        "Looking up project: ${projectId.value}"

    /**
     * Demonstrates that value classes work seamlessly in collections
     * and higher-order functions while maintaining type safety.
     */
    fun filterTaskIds(ids: List<TaskId>, prefix: String): List<TaskId> =
        ids.filter { it.value.startsWith(prefix) }

    /**
     * Demonstrates creating value class instances — zero overhead at runtime.
     * At the JVM level, TaskId("task-1") is just the String "task-1".
     */
    fun createIds(): Triple<TaskId, ProjectId, UserId> = Triple(
        TaskId("task-001"),
        ProjectId("proj-001"),
        UserId("user-001")
    )
}
