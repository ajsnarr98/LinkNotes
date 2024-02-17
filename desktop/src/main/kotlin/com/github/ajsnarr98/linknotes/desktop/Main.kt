package com.github.ajsnarr98.linknotes.desktop

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPlacement
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.github.ajsnarr98.linknotes.desktop.di.DependencyGraph
import com.github.ajsnarr98.linknotes.desktop.di.get
import com.github.ajsnarr98.linknotes.desktop.di.setNewApi
import com.github.ajsnarr98.linknotes.desktop.login.LoginScreen
import com.github.ajsnarr98.linknotes.desktop.navigation.NavController
import com.github.ajsnarr98.linknotes.desktop.navigation.WindowInfo
import com.github.ajsnarr98.linknotes.desktop.navigation.castDrawState
import com.github.ajsnarr98.linknotes.desktop.res.AmericanEnglishStringRes
import com.github.ajsnarr98.linknotes.desktop.res.ImageRes
import com.github.ajsnarr98.linknotes.desktop.res.LinkNotesDesktopTheme
import com.github.ajsnarr98.linknotes.desktop.res.StringRes
import com.github.ajsnarr98.linknotes.desktop.util.DefaultDispatcherProvider
import com.github.ajsnarr98.linknotes.desktop.util.DesktopLoggingProvider
import com.github.ajsnarr98.linknotes.network.auth.FirebaseAuthApi
import com.github.ajsnarr98.linknotes.network.http.MoshiHelper
import com.github.ajsnarr98.linknotes.network.http.OkHttpHelper
import com.github.ajsnarr98.linknotes.network.http.RetrofitBuilder
import com.github.ajsnarr98.linknotes.network.logging.LoggingProvider
import com.github.ajsnarr98.linknotes.network.util.DispatcherProvider
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.OkHttpClient
import kotlin.coroutines.CoroutineContext

fun main() = application {
    val mainContext: CoroutineContext = remember { Dispatchers.Main + Job() }

    val dependencyGraph = remember {
        DependencyGraph().setDependencies {
            set(DispatcherProvider::class) { DefaultDispatcherProvider() }
            set(CoroutineContext::class) { mainContext }
            set(CoroutineScope::class, dependencies = setOf(CoroutineContext::class)) { deps ->
                CoroutineScope(deps.get<CoroutineContext>())
            }
            set(LoggingProvider::class) { DesktopLoggingProvider() }
            set(StringRes::class) { AmericanEnglishStringRes() }
            set(ImageRes::class) { ImageRes }

            // http stuff
            set(Moshi::class) { MoshiHelper.buildMoshi() }
            set(OkHttpClient::class, dependencies = setOf(LoggingProvider::class)) { deps ->
                OkHttpHelper.buildOkHttpClient(
                    loggingProvider = deps.get()
                )
            }
            set(Call.Factory::class, dependencies = setOf(OkHttpClient::class)) { deps -> deps.get<OkHttpClient>() }

            set(RetrofitBuilder::class, dependencies = setOf(Call.Factory::class, Moshi::class)) { deps ->
                RetrofitBuilder(
                    callFactory = deps.get(),
                    moshi = deps.get(),
                )
            }

            // build api instances
            setNewApi<FirebaseAuthApi>(FirebaseAuthApi.BASE_URL)

            // login

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
            // push first screen
            push(WindowInfo.Tag.Main, LoginScreen::class)
        }
    }

    for (window in navController.windows()) {
        LinkNotesDesktopTheme {
            navController.drawWindow(this, WindowInfo.Tag.Main)
        }
    }
}