package com.github.ajsnarr98.linknotes.desktop.navigation

import androidx.compose.runtime.Composable
import com.github.ajsnarr98.linknotes.desktop.di.DependencyGraph
import java.util.LinkedList
import kotlin.reflect.KClass

class NavController(
    val dependencyGraph: DependencyGraph,
) {
    private val backstack: LinkedList<Screen> = LinkedList()

    fun <T : Screen> push(screenType: KClass<T>, args: Map<String, Any?>): T {
        val screen: T = instantiate(screenType, args)
        backstack.push(screen)
        return screen
    }

    fun pop(): Screen = backstack.pop()

    @Composable
    fun currentScreen() = backstack.peek().draw()

    private fun <T : Screen> instantiate(screenType: KClass<T>, args: Map<String, Any?>): T {
        return screenType.constructors.first { constructor ->
            constructor.parameters.size == 2
                    && constructor.parameters[0].type.classifier == NavController::class
                    && constructor.parameters[1].type.classifier == args::class
        }.call(this, args)
    }
}