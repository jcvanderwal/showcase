package com.showcase.kotlin.slice.coroutines

import com.showcase.kotlin.domain.event.DomainEvent
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Demonstrates Kotlin channel-based communication between a command handler and event consumers.
 *
 * Key Kotlin features showcased:
 * - `Channel` for CSP-style (Communicating Sequential Processes) communication
 * - `SendChannel` / `ReceiveChannel` for typed, directional communication
 * - Buffered channels for decoupled producer/consumer
 * - `for (event in channel)` iteration that suspends until elements are available
 * - Structured concurrency: channel consumers are scoped coroutines
 *
 * Requirements: 4.4
 *
 * Java 25 has no direct equivalent to channels. The closest alternatives are
 * `BlockingQueue` (blocks threads) or `Flow` (different paradigm). Channels
 * provide suspend-based, backpressure-aware communication between coroutines.
 */
class ChannelEventBus(
    bufferSize: Int = Channel.BUFFERED
) {
    /**
     * The internal channel that carries domain events.
     *
     * - `Channel.BUFFERED` uses a default buffer (64 elements)
     * - Producers suspend when the buffer is full (backpressure)
     * - Consumers suspend when the buffer is empty
     */
    private val channel = Channel<DomainEvent>(bufferSize)

    /**
     * Exposes a send-only view of the channel for producers (e.g., CommandHandler).
     *
     * Type safety: producers can only send, not receive.
     */
    val sendChannel: SendChannel<DomainEvent> get() = channel

    /**
     * Exposes a receive-only view of the channel for consumers.
     *
     * Type safety: consumers can only receive, not send.
     */
    val receiveChannel: ReceiveChannel<DomainEvent> get() = channel

    /**
     * Publishes events to the channel.
     *
     * This is a suspend function — it will suspend if the channel buffer is full,
     * providing natural backpressure without blocking a thread.
     */
    suspend fun publish(events: List<DomainEvent>) {
        for (event in events) {
            channel.send(event)
        }
    }

    /**
     * Starts consuming events from the channel and dispatching to handlers.
     *
     * Demonstrates structured concurrency with channels:
     * - Each handler gets its own coroutine via `launch`
     * - The `for` loop suspends when no events are available
     * - Cancelling the coroutine scope stops consumption
     *
     * Note: This fan-out pattern means each event goes to ONE handler.
     * For fan-out to ALL handlers, use broadcast or shared flow.
     */
    suspend fun consumeAndDispatch(
        handler: suspend (DomainEvent) -> Unit
    ) {
        for (event in channel) {
            handler(event)
        }
    }

    /**
     * Starts multiple concurrent consumers that process events from the channel.
     *
     * Demonstrates fan-out: events are distributed across consumers.
     * Each event is processed by exactly one consumer (work-stealing pattern).
     */
    suspend fun startWorkers(
        workerCount: Int,
        handler: suspend (DomainEvent) -> Unit
    ) = coroutineScope {
        repeat(workerCount) { workerId ->
            launch {
                for (event in channel) {
                    handler(event)
                }
            }
        }
    }

    /**
     * Closes the channel, signaling that no more events will be sent.
     *
     * After closing:
     * - Remaining buffered events can still be received
     * - New sends will throw `ClosedSendChannelException`
     * - Consumers' `for` loops will terminate after draining the buffer
     */
    fun close() {
        channel.close()
    }
}
