package com.github.ajsnarr98.linknotes.desktop.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.github.ajsnarr98.linknotes.desktop.navigation.NavController
import com.github.ajsnarr98.linknotes.desktop.navigation.Screen
import com.github.ajsnarr98.linknotes.desktop.navigation.WindowInfo

class LoginScreen(
    navController: NavController,
    args: Map<String, Any?>,
) : Screen(navController, args) {

    val controller = createUiModelController(LoginController::class)

    @Composable
    override fun draw(window: WindowInfo.Tag, windowDrawState: MutableState<WindowInfo.DrawState>) {
        TODO("Not yet implemented")
    }
}