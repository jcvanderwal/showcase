package com.showcase.java.slice.namedmethods;

import com.showcase.java.domain.command.Command;
import com.showcase.java.domain.command.CommandHandler;
import com.showcase.java.domain.event.DomainEvent;
import com.showcase.java.domain.model.Priority;

import java.util.Comparator;
import java.util.List;

/**
 * Slice 7 — Named Methods: CommandExecutor
 *
 * Java equivalent of Kotlin's `invoke` and `compareTo` operator overloading.
 * Since Java has no operator overloading, we use explicit named methods
 * like {@code execute()} instead of the {@code invoke} operator, and
 * a {@code Comparator} instead of overloaded {@code compareTo}.
 */
public final class CommandExecutor {

    private final CommandHandler handler;

    public CommandExecutor(CommandHandler handler) {
        this.handler = handler;
    }

    /**
     * Executes a command through the handler.
     * Kotlin equivalent: invoker(command) — using invoke operator
     */
    public List<DomainEvent> execute(Command command) {
        return handler.handle(command);
    }

    /**
     * Comparator for priority-based command comparison.
     * Kotlin equivalent: command1.compareTo(command2) — using compareTo operator
     */
    public static final Comparator<Command> PRIORITY_COMPARATOR = Comparator.comparingInt(cmd -> {
        return switch (cmd) {
            case Command.CreateTask c -> c.priority().ordinal();
            case Command.ChangeTaskPriority c -> c.newPriority().ordinal();
            default -> 0;
        };
    });

    /**
     * Compares two commands by priority.
     * Kotlin equivalent: command1 > command2
     */
    public static int comparePriority(Command cmd1, Command cmd2) {
        return PRIORITY_COMPARATOR.compare(cmd1, cmd2);
    }
}
