package com.github.ajsnarr98.linknotes.desktop.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.ajsnarr98.linknotes.desktop.navigation.NavController
import com.github.ajsnarr98.linknotes.desktop.navigation.Screen
import com.github.ajsnarr98.linknotes.desktop.navigation.WindowInfo
import com.github.ajsnarr98.linknotes.desktop.res.ImageRes
import com.github.ajsnarr98.linknotes.desktop.res.StringRes
import kotlinx.coroutines.flow.StateFlow

class LoginScreen(
    navController: NavController,
    args: Map<String, Any?>,
    private val stringRes: StateFlow<StringRes>,
    private val imageRes: StateFlow<ImageRes>,
) : Screen(navController, args) {

    /** Needed for auto dependency-injection **/
    constructor(
        navController: NavController,
        args: Map<String, Any?>,
    ) : this(
        navController, args,
        stringRes = navController.dependencyGraph.get(),
        imageRes = navController.dependencyGraph.get(),
    )

    private val controller = createUiModelController(LoginController::class)

    @Composable
    override fun draw(window: WindowInfo.Tag, windowDrawState: MutableState<WindowInfo.DrawState>) {
        val stringRes: StringRes by stringRes.collectAsState(context = controller.controllerContext)
        val imageRes: ImageRes by imageRes.collectAsState(context = controller.controllerContext)

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
                .fillMaxSize()
        ) {
            Text(text = stringRes.login.title, style = MaterialTheme.typography.h2)
            Spacer(modifier = Modifier.height(55.dp))
            SignInWithGoogleButton(
                onClick = { controller.onClickSignInWithGoogleButton() },
                stringRes = stringRes,
                imageRes = imageRes,
                modifier = Modifier.widthIn(max = 500.dp)
                    .fillMaxWidth()
                    .height(55.dp)
                    .padding(40.dp, 0.dp, 40.dp, 0.dp)
            )
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SignInWithGoogleButton(
    onClick: () -> Unit,
    imageRes: ImageRes,
    stringRes: StringRes,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(50.dp),
        modifier = modifier,
    ) {
        Image(
            painter = imageRes.icGoogle(),
            contentDescription = null,
            modifier = Modifier
                .background(
                    color = Color.White,
                    shape = RoundedCornerShape(CornerSize(12.dp)),
                )
                .padding(2.dp)
        )
        Text(
            text = stringRes.login.signInWithGoogle,
            style = MaterialTheme.typography.subtitle1,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(35.dp))
    }
}
