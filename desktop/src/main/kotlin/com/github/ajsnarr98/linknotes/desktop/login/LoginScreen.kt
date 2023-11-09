package com.github.ajsnarr98.linknotes.desktop.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
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

class LoginScreen(
    navController: NavController,
    args: Map<String, Any?>,
    private val stringRes: StringRes,
    private val imageRes: ImageRes,
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

    val controller = createUiModelController(LoginController::class)

    @Composable
    override fun draw(window: WindowInfo.Tag, windowDrawState: MutableState<WindowInfo.DrawState>) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(20.dp)
                .fillMaxSize()
        ) {
            Text(text = stringRes.login.title, style = MaterialTheme.typography.h2)
            Spacer(modifier = Modifier.height(55.dp))

            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = { },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth()
                        .height(55.dp)
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
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}