package com.github.ajsnarr98.linknotes.desktop.navigation

import com.github.ajsnarr98.linknotes.desktop.di.DependencyGraph
import com.github.ajsnarr98.linknotes.network.util.DispatcherProvider
import kotlinx.coroutines.*
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

    val dispatcherProvider: DispatcherProvider = dependencyGraph.get<DispatcherProvider>().value

    @OptIn(ExperimentalCoroutinesApi::class)
    val controllerContext: CoroutineContext = dependencyGraph.get<CoroutineScope>().value.newCoroutineContext(
        dispatcherProvider.main()
    )
    val controllerScope: CoroutineScope = CoroutineScope(controllerContext)

    override fun close() {
        controllerScope.cancel()
    }
}