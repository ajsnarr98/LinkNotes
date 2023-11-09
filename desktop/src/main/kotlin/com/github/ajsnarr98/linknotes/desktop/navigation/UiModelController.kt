package com.github.ajsnarr98.linknotes.desktop.navigation

import com.github.ajsnarr98.linknotes.desktop.di.DependencyGraph
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlin.coroutines.CoroutineContext

/**
 * A class that controls the state of some UI.
 *
 * Every [UiModelController] must have a secondary constructor that takes in nothing but
 * a [DependencyGraph] and arguments Map<String, Any?>.
 */
abstract class UiModelController(
    dependencyGraph: DependencyGraph,
) : AutoCloseable {
    protected val controllerScope: CoroutineScope = CoroutineScope(dependencyGraph[CoroutineContext::class])

    override fun close() {
        controllerScope.cancel()
    }
}