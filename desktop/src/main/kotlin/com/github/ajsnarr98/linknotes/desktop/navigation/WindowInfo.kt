package com.github.ajsnarr98.linknotes.desktop.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.FrameWindowScope
import androidx.compose.ui.window.WindowState

typealias WindowDrawInstructions = @Composable ApplicationScope.(
    drawState: MutableState<out WindowInfo.DrawState>,
    content: @Composable FrameWindowScope.() -> Unit,
) -> Unit

object WindowInfo {

    sealed interface Tag {
        /**
         * Special instance of Window representing the current window of a screen.
         */
        data object Current : Tag
        data object Main : Tag
    }

    /**
     *
     */
    sealed interface DrawState {
        data class Main(
            val windowState: WindowState,
            val onCloseRequest: () -> Unit,
        ) : DrawState
    }
}

inline fun <reified T : WindowInfo.DrawState> castDrawState(uncastDrawState: State<WindowInfo.DrawState>): State<T> = derivedStateOf {
    uncastDrawState.value as? T
        ?: throw IllegalArgumentException("Invalid draw state. Expected ${T::class} but got ${uncastDrawState::class}")
}
