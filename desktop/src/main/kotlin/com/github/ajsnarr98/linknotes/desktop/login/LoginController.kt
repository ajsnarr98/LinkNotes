package com.github.ajsnarr98.linknotes.desktop.login

import com.github.ajsnarr98.linknotes.desktop.di.DependencyGraph
import com.github.ajsnarr98.linknotes.desktop.navigation.UiModelController
import com.github.ajsnarr98.linknotes.network.util.DispatcherProvider
import kotlinx.coroutines.CoroutineScope

class LoginController(
    dispatcherProvider: DispatcherProvider,
    appScope: CoroutineScope,
) : UiModelController(
    dispatcherProvider = dispatcherProvider,
    appScope = appScope,
) {
    constructor(
        dependencyGraph: DependencyGraph,
        args: Map<String, Any?>,
    ) : this(
        dispatcherProvider = dependencyGraph.get<DispatcherProvider>().value,
        appScope = dependencyGraph.get<CoroutineScope>().value
    )

    fun onClickSignInWithGoogleButton() {

    }
}