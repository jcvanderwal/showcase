package com.showcase.java.slice.delegation;

import java.time.Instant;
import java.util.function.Consumer;

/**
 * Slice 6 — Delegation Pattern (Req 7)
 *
 * Java 25 equivalent of Kotlin's property delegates (lazy, observable, custom).
 * All delegation logic must be hand-coded — Java has no built-in property
 * delegation mechanism.
 */
public class AuditWrapper {

    private final Consumer<String> log;

    // --- Lazy (manual) ---
    private String createdAt;
    private boolean createdAtInitialized = false;

    // --- Observable (manual) ---
    private String status = "OPEN";

    // --- Audited (manual) ---
    private String assignee;

    public AuditWrapper() {
        this(System.out::println);
    }

    public AuditWrapper(Consumer<String> log) {
        this.log = log;
    }

    /** Lazy — computed once on first access. */
    public String getCreatedAt() {
        if (!createdAtInitialized) {
            log.accept("Computing createdAt (lazy init)");
            createdAt = Instant.now().toString();
            createdAtInitialized = true;
        }
        return createdAt;
    }

    /** Observable — fires callback on every change. */
    public String getStatus() {
        return status;
    }

    public void setStatus(String newStatus) {
        var old = this.status;
        this.status = newStatus;
        log.accept("OBSERVABLE status: %s -> %s".formatted(old, newStatus));
    }

    /** Audited — logs reads and writes. */
    public String getAssignee() {
        log.accept("READ  assignee = %s".formatted(assignee));
        return assignee;
    }

    public void setAssignee(String assignee) {
        log.accept("WRITE assignee: %s -> %s".formatted(this.assignee, assignee));
        this.assignee = assignee;
    }
}
