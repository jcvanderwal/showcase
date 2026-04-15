package com.showcase.kotlin.slice.delegation

import kotlin.properties.Delegates
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Slice 6 — Delegation Pattern (Req 7)
 *
 * Demonstrates Kotlin's property delegation: lazy, observable, and a custom
 * audit-logging delegate. Java 25 has no equivalent — all of this must be
 * hand-coded with getters/setters and wrapper logic.
 */

// --- Custom property delegate for audit logging ---

class AuditLogDelegate<T>(
    private var value: T,
    private val log: (String) -> Unit = ::println
) : ReadWriteProperty<Any?, T> {

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        log("READ  ${property.name} = $value")
        return value
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        log("WRITE ${property.name}: ${this.value} -> $value")
        this.value = value
    }
}

fun <T> audited(initial: T, log: (String) -> Unit = ::println): AuditLogDelegate<T> =
    AuditLogDelegate(initial, log)

// --- Showcase class combining lazy, observable, and custom delegates ---

class AuditDelegate(private val log: (String) -> Unit = ::println) {

    /** Lazy delegate — computed once on first access. */
    val createdAt: String by lazy {
        log("Computing createdAt (lazy init)")
        java.time.Instant.now().toString()
    }

    /** Observable delegate — fires callback on every change. */
    var status: String by Delegates.observable("OPEN") { prop, old, new ->
        log("OBSERVABLE ${prop.name}: $old -> $new")
    }

    /** Custom audit-logging delegate. */
    var assignee: String? by audited(null, log)
}
