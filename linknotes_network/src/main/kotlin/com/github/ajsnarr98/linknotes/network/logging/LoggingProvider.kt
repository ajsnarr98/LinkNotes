package com.github.ajsnarr98.linknotes.network.logging

interface LoggingProvider {
    /**
     * Error logging. TODO
     */
    fun e(error: Throwable)

    /**
     * Error logging. TODO
     */
    fun e(message: String)

    /**
     * Warning logging. TODO
     */
    fun w(message: String)

    /**
     * Debug logging: TODO
     */
    fun d(message: String)
}