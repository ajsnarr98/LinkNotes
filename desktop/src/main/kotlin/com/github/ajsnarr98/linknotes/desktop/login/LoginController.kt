package com.github.ajsnarr98.linknotes.desktop.login

import com.github.ajsnarr98.linknotes.desktop.di.DependencyGraph
import com.github.ajsnarr98.linknotes.desktop.navigation.UiModelController
import com.github.ajsnarr98.linknotes.network.auth.AuthRepository
import com.github.ajsnarr98.linknotes.network.util.DispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class LoginController(
    dispatcherProvider: DispatcherProvider,
    appScope: CoroutineScope,
    private val authRepository: AuthRepository,
) : UiModelController(
    dispatcherProvider = dispatcherProvider,
    appScope = appScope,
) {
    constructor(
        dependencyGraph: DependencyGraph,
        args: Map<String, Any?>,
    ) : this(
        dispatcherProvider = dependencyGraph.get<DispatcherProvider>().value,
        appScope = dependencyGraph.get<CoroutineScope>().value,
        authRepository = dependencyGraph.get<AuthRepository>().value,
    )

    fun onClickSignInWithGoogleButton() {
        controllerScope.launch {
            authRepository.signInWithGoogle()
        }
    }
}