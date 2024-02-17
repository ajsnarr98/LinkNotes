package com.github.ajsnarr98.linknotes.network.logging

interface LoggingProvider {
    /**
     * Error logging. TODO
     */
    fun e()

    /**
     * Debug logging: TODO
     */
    fun d(message: String)
}