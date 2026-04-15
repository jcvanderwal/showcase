package com.showcase.kotlin.slice.dslbuilders

import com.showcase.kotlin.domain.model.Priority
import java.time.LocalDate

/**
 * Slice 5 — DSL Builders (Req 6)
 *
 * Type-safe DSL for constructing ProjectAggregate configurations with nested
 * Task definitions. Uses lambdas with receivers and @DslMarker to prevent
 * scope leaking — something Java 25 cannot replicate.
 */

@DslMarker
annotation class ProjectDslMarker

/** Immutable result of the DSL. */
data class ProjectConfig(
    val name: String,
    val description: String?,
    val tasks: List<TaskConfig>
)

data class TaskConfig(
    val title: String,
    val priority: Priority,
    val dueDate: LocalDate?,
    val tags: Set<String>
)

/** Top-level DSL entry point. */
fun project(block: ProjectBuilder.() -> Unit): ProjectConfig {
    val builder = ProjectBuilder().apply(block)
    return builder.build()
}

@ProjectDslMarker
class ProjectBuilder {
    var name: String = ""
    var description: String? = null
    private val tasks = mutableListOf<TaskConfig>()

    fun task(block: TaskBuilder.() -> Unit) {
        tasks += TaskBuilder().apply(block).build()
    }

    fun build(): ProjectConfig {
        require(name.isNotBlank()) { "Project name is required" }
        return ProjectConfig(name, description, tasks.toList())
    }
}

@ProjectDslMarker
class TaskBuilder {
    var title: String = ""
    var priority: Priority = Priority.MEDIUM
    var dueDate: LocalDate? = null
    private val tags = mutableSetOf<String>()

    fun tag(value: String) { tags += value }
    fun tags(vararg values: String) { tags += values }

    fun build(): TaskConfig {
        require(title.isNotBlank()) { "Task title is required" }
        return TaskConfig(title, priority, dueDate, tags.toSet())
    }
}
