package com.github.ajsnarr98.linknotes.network

import com.github.ajsnarr98.linknotes.network.util.RealTimeProvider
import com.github.ajsnarr98.linknotes.network.util.TimeProvider

object GlobalScopedDependencies {
    var timeProvider: TimeProvider = RealTimeProvider()
}