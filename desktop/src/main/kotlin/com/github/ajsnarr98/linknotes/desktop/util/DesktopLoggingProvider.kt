package com.github.ajsnarr98.linknotes.desktop.util

import com.github.ajsnarr98.linknotes.network.logging.LoggingProvider

class DesktopLoggingProvider : LoggingProvider {
    override fun e(error: Throwable) {
        error.printStackTrace()
    }

    override fun e(message: String) {
        println(message)
    }

    override fun w(message: String) {
        println(message)
    }

    override fun d(message: String) {
        println(message)
    }
}