package com.showcase.java.domain.model;

import java.time.Instant;

public sealed interface TaskStatus {
    record Open() implements TaskStatus {}
    record InProgress(UserId assignee) implements TaskStatus {}
    record Blocked(String reason) implements TaskStatus {}
    record Completed(Instant completedAt) implements TaskStatus {}
}
