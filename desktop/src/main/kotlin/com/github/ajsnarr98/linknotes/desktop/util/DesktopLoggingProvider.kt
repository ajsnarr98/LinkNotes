package com.github.ajsnarr98.linknotes.desktop.util

import com.github.ajsnarr98.linknotes.network.logging.LoggingProvider

class DesktopLoggingProvider : LoggingProvider {
    override fun e() {
        TODO("Not yet implemented")
    }

    override fun d(message: String) {
        println(message)
    }
}