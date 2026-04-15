package com.showcase.kotlin.slice.operators

import com.showcase.kotlin.domain.command.Command
import com.showcase.kotlin.domain.command.CommandHandler
import com.showcase.kotlin.domain.event.DomainEvent

/**
 * Slice 7 — Operator Overloading: CommandInvoke
 *
 * Demonstrates Kotlin's `invoke` operator overloading for executing a Command
 * through a handler, and `compareTo` for priority-based comparison.
 * Java has no equivalent — it requires named methods like `execute()`.
 */

/**
 * Wraps a CommandHandler to support the `invoke` operator.
 * Usage: val events = commandInvoker(someCommand)
 */
class CommandInvoker(private val handler: CommandHandler) {

    /**
     * Overloads the `invoke` operator so the handler can be called like a function.
     * Usage: val events = invoker(Command.CompleteTask(taskId))
     */
    suspend operator fun invoke(command: Command): List<DomainEvent> =
        handler.handle(command)
}

/**
 * Overloads `compareTo` on Command for priority-based comparison.
 * Commands are compared by their ordinal priority when they carry priority info.
 * Higher priority commands sort first (CRITICAL > HIGH > MEDIUM > LOW).
 */
operator fun Command.compareTo(other: Command): Int {
    val thisPriority = when (this) {
        is Command.CreateTask -> this.priority.ordinal
        is Command.ChangeTaskPriority -> this.newPriority.ordinal
        else -> 0
    }
    val otherPriority = when (other) {
        is Command.CreateTask -> other.priority.ordinal
        is Command.ChangeTaskPriority -> other.newPriority.ordinal
        else -> 0
    }
    return thisPriority.compareTo(otherPriority)
}
