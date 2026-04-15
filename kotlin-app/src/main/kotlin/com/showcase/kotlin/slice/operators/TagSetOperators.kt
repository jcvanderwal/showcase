package com.showcase.kotlin.slice.operators

import com.showcase.kotlin.domain.model.TaskSummary

/**
 * Slice 7 — Operator Overloading: TagSetOperators
 *
 * Demonstrates Kotlin's operator overloading with the `plus` (+) operator
 * for merging tag sets on tasks. In Java, this requires named methods like
 * `mergeTags()` since Java has no operator overloading support.
 */

/**
 * Overloads the `+` operator on TaskSummary to merge tag sets.
 * Usage: val merged = task1 + task2  // merges tags from both tasks
 */
operator fun TaskSummary.plus(other: TaskSummary): Set<String> =
    this.tags + other.tags

/**
 * Overloads the `+` operator to add a single tag to a TaskSummary's tag set.
 * Usage: val newTags = task + "urgent"
 */
operator fun TaskSummary.plus(tag: String): Set<String> =
    this.tags + tag

/**
 * Overloads the `+` operator to merge an additional tag set into a TaskSummary's tags.
 * Usage: val newTags = task + setOf("backend", "api")
 */
operator fun TaskSummary.plus(tags: Set<String>): Set<String> =
    this.tags + tags
