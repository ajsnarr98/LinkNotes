package com.github.ajsnarr98.linknotes.desktop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.github.ajsnarr98.linknotes.desktop.di.DependencyGraph
import java.util.LinkedList
import kotlin.reflect.KClass
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.starProjectedType

/**
 * Every screen must have a secondary constructor that takes in nothing but
 * a [NavController] and arguments Map<String, Any?>.
 */
abstract class Screen(
    protected val navController: NavController,
    private val args: Map<String, Any?>,
) {
    private val toClose: MutableList<AutoCloseable> = LinkedList()

    @Composable
    abstract fun draw(window: WindowInfo.Tag, windowDrawState: MutableState<WindowInfo.DrawState>)

    /**
     * Create an instance of a UIModelController
     */
    protected fun <T : UiModelController> createUiModelController(controllerType: KClass<T>): T {
        return controllerType.constructors.first { constructor ->
            constructor.parameters.size == 2
                    && constructor.parameters[0].type.classifier?.starProjectedType?.isSupertypeOf(DependencyGraph::class.starProjectedType) == true
                    && constructor.parameters[1].type.classifier?.starProjectedType?.isSupertypeOf(args::class.starProjectedType) == true
        }.call(this.navController.dependencyGraph, args).also { controller ->
            toClose.add(controller)
        }
    }

    open fun onBackPressed() {
        navController.pop(WindowInfo.Tag.Current)
        onScreenDismissed()
    }

    open fun onScreenDismissed() {
        for (c in toClose) {
            c.close()
        }
    }
}