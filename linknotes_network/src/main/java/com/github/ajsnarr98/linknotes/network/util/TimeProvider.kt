package com.github.ajsnarr98.linknotes.network.util

import java.util.Date

interface TimeProvider {
    fun now(): Date
}

class RealTimeProvider() : TimeProvider {
    override fun now(): Date {
        return Date()
    }
}
