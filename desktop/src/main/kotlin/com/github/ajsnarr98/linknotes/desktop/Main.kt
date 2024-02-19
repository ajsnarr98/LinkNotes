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
import com.github.ajsnarr98.linknotes.desktop.login.api.AuthProviderDesktop
import com.github.ajsnarr98.linknotes.desktop.login.api.GoogleOAuth
import com.github.ajsnarr98.linknotes.desktop.login.api.RealGoogleOAuth
import com.github.ajsnarr98.linknotes.desktop.navigation.NavController
import com.github.ajsnarr98.linknotes.desktop.navigation.WindowInfo
import com.github.ajsnarr98.linknotes.desktop.navigation.castDrawState
import com.github.ajsnarr98.linknotes.desktop.res.AmericanEnglishStringRes
import com.github.ajsnarr98.linknotes.desktop.res.ImageRes
import com.github.ajsnarr98.linknotes.desktop.res.LinkNotesDesktopTheme
import com.github.ajsnarr98.linknotes.desktop.res.StringRes
import com.github.ajsnarr98.linknotes.desktop.storage.UserStore
import com.github.ajsnarr98.linknotes.desktop.util.DefaultDispatcherProvider
import com.github.ajsnarr98.linknotes.desktop.util.DesktopLoggingProvider
import com.github.ajsnarr98.linknotes.network.auth.AuthRepository
import com.github.ajsnarr98.linknotes.network.auth.DefaultAuthRepository
import com.github.ajsnarr98.linknotes.network.auth.FirebaseAuthApi
import com.github.ajsnarr98.linknotes.network.domain.User
import com.github.ajsnarr98.linknotes.network.http.MoshiHelper
import com.github.ajsnarr98.linknotes.network.http.OkHttpHelper
import com.github.ajsnarr98.linknotes.network.http.RetrofitBuilder
import com.github.ajsnarr98.linknotes.network.logging.LoggingProvider
import com.github.ajsnarr98.linknotes.network.storage.local.LocalStorage
import com.github.ajsnarr98.linknotes.network.util.DispatcherProvider
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.*
import okhttp3.Call
import okhttp3.OkHttpClient
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.typeOf

fun main() = application {
    val mainContext: CoroutineContext = remember { Dispatchers.Main + Job() }

    val dependencyGraph = remember {
        DependencyGraph().setDependencies {
            set<DispatcherProvider> { DefaultDispatcherProvider() }
            set<CoroutineContext> { mainContext }
            set<CoroutineScope>(dependencies = setOf(typeOf<CoroutineContext>())) { dependencies ->
                CoroutineScope(dependencies.get<CoroutineContext>())
            }
            set<LoggingProvider> { DesktopLoggingProvider() }
            set<StringRes> { AmericanEnglishStringRes() }
            set<ImageRes> { ImageRes }

            // http stuff
            set<Moshi> { MoshiHelper.buildMoshi() }
            set<JsonFactory> { GsonFactory.getDefaultInstance() }
            set<OkHttpClient>(dependencies = setOf(typeOf<LoggingProvider>())) { dependencies ->
                OkHttpHelper.buildOkHttpClient(
                    loggingProvider = dependencies.get()
                )
            }
            set<Call.Factory>(dependencies = setOf(typeOf<OkHttpClient>())) { dependencies -> dependencies.get<OkHttpClient>() }

            set<RetrofitBuilder>(dependencies = setOf(typeOf<Call.Factory>(), typeOf<Moshi>())) { dependencies ->
                RetrofitBuilder(
                    callFactory = dependencies.get(),
                    moshi = dependencies.get(),
                )
            }

            // build api instances
            setNewApi<FirebaseAuthApi>(FirebaseAuthApi.BASE_URL)

            // login
            set<GoogleOAuth>(dependencies = setOf(
                typeOf<JsonFactory>(),
                typeOf<Moshi>(),
            )) { dependencies ->
                RealGoogleOAuth(
                    jsonFactory = dependencies.get(),
                    moshi = dependencies.get(),
                )
            }
            set<AuthRepository.AuthProvider>(dependencies = setOf(
                typeOf<FirebaseAuthApi>(),
                typeOf<DispatcherProvider>(),
                typeOf<GoogleOAuth>(),
            )) { dependencies ->
                AuthProviderDesktop(
                    authApi = dependencies.get(),
                    dispatcherProvider = dependencies.get(),
                    googleOAuth = dependencies.get(),
                )
            }
            set<LocalStorage<User>> { UserStore() }
            set<AuthRepository>(dependencies = setOf(
                typeOf<AuthRepository.AuthProvider>(),
                typeOf<LocalStorage<User>>(),
            )) { dependencies ->
                DefaultAuthRepository(
                    authProvider = dependencies.get(),
                    accountStore = dependencies.get(),
                )
            }
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