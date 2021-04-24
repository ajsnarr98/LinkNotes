package com.github.ajsnarr98.linknotes.util

import kotlinx.coroutines.delay


/**
 * A delayed task that can return something.
 */
class DelayedTask<R>(private val delayMS: Long, private val task: suspend () -> R) {
    private val callbacks = mutableSetOf<suspend (result: R) -> Unit>()
    @Volatile private var started = false

    /**
     * Starts this task. Delays here.
     *
     * Will not start if this task has already started.
     */
    suspend fun start() {
        synchronized(this) {
            // don't start if already started
            if (started) {
                return
            } else {
                started = true
            }
        }
        delay(delayMS)
        val result = task()
        val callbacksCopy: Collection<suspend (result: R) -> Unit>
        synchronized(this) {
             callbacksCopy = callbacks.map { it }
        }
        callbacksCopy.forEach { it(result) }
    }

    fun addCallback(callback: suspend (result: R) -> Unit) {
        synchronized(this) {
            callbacks.add(callback)
        }
    }
}