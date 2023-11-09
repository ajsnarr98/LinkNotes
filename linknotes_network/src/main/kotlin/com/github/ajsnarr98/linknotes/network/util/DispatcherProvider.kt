package com.github.ajsnarr98.linknotes.network.util

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

interface DispatcherProvider {
    fun default(): CoroutineDispatcher
    fun main(): CoroutineDispatcher
    fun unconfined(): CoroutineDispatcher
    fun io(): CoroutineDispatcher
}

class DefaultDispatcherProvider : DispatcherProvider {
    override fun default(): CoroutineDispatcher = Dispatchers.Default
    override fun main(): CoroutineDispatcher = Dispatchers.Main
    override fun unconfined(): CoroutineDispatcher = Dispatchers.Unconfined
    override fun io(): CoroutineDispatcher = Dispatchers.IO
}