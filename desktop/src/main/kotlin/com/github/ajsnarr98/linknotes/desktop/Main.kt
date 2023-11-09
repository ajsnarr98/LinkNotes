package com.github.ajsnarr98.linknotes.desktop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.ajsnarr98.linknotes.desktop.di.DependencyGraph
import com.github.ajsnarr98.linknotes.desktop.login.LoginScreen
import com.github.ajsnarr98.linknotes.desktop.navigation.NavController
import com.github.ajsnarr98.linknotes.desktop.navigation.WindowInfo
import com.github.ajsnarr98.linknotes.desktop.navigation.castDrawState
import com.github.ajsnarr98.linknotes.network.util.DefaultDispatcherProvider
import com.github.ajsnarr98.linknotes.network.util.DispatcherProvider
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

fun main() = application {
    val mainContext: CoroutineContext = remember { Dispatchers.Main + Job() }

    val dependencyGraph = remember {
        DependencyGraph().setDependencies {
            set(DispatcherProvider::class) { DefaultDispatcherProvider() }
            set(CoroutineContext::class) { mainContext }
        }
    }

    val mainWindowState = rememberWindowState(
        placement = WindowPlacement.Maximized,
    )
    val navController = remember {
        NavController(dependencyGraph).apply {
            registerWindow(
                window = WindowInfo.Tag.Main,
                drawState = WindowInfo.DrawState.Main(
                    windowState = mainWindowState,
                    onCloseRequest = {
                        runBlocking {
                            withTimeout(10000L) {
                                mainContext.cancel()
                            }
                        }
                        this@application.exitApplication()
                    }
                )
            ) { unCastDrawState, content ->
                val drawState: WindowInfo.DrawState.Main by castDrawState(unCastDrawState)
                Window(
                    onCloseRequest = drawState.onCloseRequest,
                    state = drawState.windowState,
                    content = content,
                )
            }
            push(WindowInfo.Tag.Main, LoginScreen::class)
        }
    }

    for (window in navController.windows()) {
        navController.drawWindow(this, WindowInfo.Tag.Main)
    }
}