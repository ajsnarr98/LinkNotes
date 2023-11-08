package com.github.ajsnarr98.linknotes.desktop.navigation

import androidx.compose.runtime.Composable
import com.github.ajsnarr98.linknotes.desktop.di.DependencyGraph
import kotlin.reflect.KClass

/**
 * Every screen must have a secondary constructor that takes in nothing but
 * a [NavController] and arguments Map<String, Any?>.
 */
abstract class Screen(
    val navController: NavController,
    private val args: Map<String, Any?>,
) {
    @Composable
    abstract fun draw()

    /**
     * Create an instance of a UIModelController
     */
    protected fun <T : UiModelController> createUiModelController(controllerType: KClass<T>): T {
        return controllerType.constructors.first { constructor ->
            constructor.parameters.size == 2
                    && constructor.parameters[0].type.classifier == DependencyGraph::class
                    && constructor.parameters[1].type.classifier == args::class
        }.call(this.navController.dependencyGraph, args)
    }
}