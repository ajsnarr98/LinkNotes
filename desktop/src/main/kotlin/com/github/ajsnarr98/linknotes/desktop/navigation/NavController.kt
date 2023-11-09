package com.github.ajsnarr98.linknotes.desktop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.window.ApplicationScope
import com.github.ajsnarr98.linknotes.desktop.di.DependencyGraph
import java.util.LinkedList
import kotlin.reflect.KClass
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.full.starProjectedType

class NavController(
    val dependencyGraph: DependencyGraph,
) {
    private val windowBackstacks: MutableMap<WindowInfo.Tag, LinkedList<Screen>> = LinkedHashMap()
    private val windows: MutableMap<WindowInfo.Tag, Pair<MutableState<WindowInfo.DrawState>, WindowDrawInstructions>> = mutableMapOf()

    /**
     * Needs to be called exactly once for each [WindowInfo.Tag] that is used.
     *
     * Should push the first screen of this window immediately after regitering.
     */
    fun registerWindow(window: WindowInfo.Tag, drawState: WindowInfo.DrawState, drawInstructions: WindowDrawInstructions) {
        if (windows.keys.contains(window)) throw IllegalArgumentException("Window $window is already registered")

        windows[window] = mutableStateOf(drawState) to drawInstructions
        windowBackstacks[window] = LinkedList()
    }

    fun <T : Screen> Screen.push(window: WindowInfo.Tag, screenType: KClass<T>, args: Map<String, Any?> = emptyMap()): T {
        val trueWindow = when (window) {
            is WindowInfo.Tag.Current -> windowBackstacks.entries.firstOrNull { (_, screens) -> screens.contains(this) }?.key
                ?: throw IllegalStateException("Could not find the window this screen is on")
            else -> window
        }

        return this@NavController.push(trueWindow, screenType, args)
    }

    fun <T : Screen> push(window: WindowInfo.Tag, screenType: KClass<T>, args: Map<String, Any?> = emptyMap()): T {
        if (window == WindowInfo.Tag.Current) throw IllegalArgumentException(
            "${WindowInfo.Tag.Current} is only a valid window type when passed from a ${Screen::class} context"
        )

        val screen: T = instantiate(screenType, args)
        windowBackstacks.pushToWindow(window, screen)
        return screen
    }

    fun Screen.pop(window: WindowInfo.Tag): Screen {
        return this@NavController.pop(
            when (window) {
                is WindowInfo.Tag.Current -> windowBackstacks.entries.firstOrNull { (_, screens) -> screens.contains(this) }?.key
                    ?: throw IllegalStateException("Could not find the window this screen is on")
                else -> window
            }
        )
    }

    fun pop(window: WindowInfo.Tag): Screen {
        if (window == WindowInfo.Tag.Current) throw IllegalArgumentException(
            "${WindowInfo.Tag.Current} is only a valid window type when passed from a ${Screen::class} context"
        )
        return windowBackstacks.popToWindow(window)
    }

    /**
     * Gets all registered windows that have a screen up.
     */
    fun windows(): Iterable<WindowInfo.Tag> = windows.keys
        .filter { window -> windowBackstacks[window]?.isNotEmpty() == true }

    /**
     * Draws given window and the current screen on it.
     */
    @Composable
    fun drawWindow(applicationScope: ApplicationScope, window: WindowInfo.Tag) {
        val (drawState, drawInstructions) =  windows[window]
            ?: throw throw IllegalArgumentException("No matching window found")

        val screen = windowBackstacks[window]?.peek()
            ?: throw IllegalArgumentException("No screens for window found")

        with(applicationScope) {
            drawInstructions(drawState) { screen.draw(window, drawState) }
        }
    }

    private fun <T : Screen> instantiate(screenType: KClass<T>, args: Map<String, Any?>): T {
        return screenType.constructors.first { constructor ->
            constructor.parameters.size == 2
                    && constructor.parameters[0].type.classifier?.starProjectedType?.isSupertypeOf(NavController::class.starProjectedType) == true
                    && constructor.parameters[1].type.classifier?.starProjectedType?.isSupertypeOf(args::class.starProjectedType) == true
        }.call(this, args)
    }

    private fun MutableMap<WindowInfo.Tag, LinkedList<Screen>>.pushToWindow(
        window: WindowInfo.Tag,
        screen: Screen,
    ) {
        this[window] = (this[window] ?: LinkedList()).apply { push(screen) }
    }

    private fun MutableMap<WindowInfo.Tag, LinkedList<Screen>>.popToWindow(window: WindowInfo.Tag): Screen {
        var popped: Screen
        this[window] = (this[window] ?: LinkedList()).apply { popped = pop() }
        return popped
    }
}
