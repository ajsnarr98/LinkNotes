package com.github.ajsnarr98.linknotes

import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.github.ajsnarr98.linknotes.util.SoftKeyboardListener

abstract class BaseActivity : AppCompatActivity() {

    /**
     * Creates a [SoftKeyboardListener] using the given lambdas.
     */
    fun createKeyboardListener(
        onKeyboardShow: () -> Unit = {},
        onKeyboardHide: () -> Unit = {},
    ): SoftKeyboardListener = SoftKeyboardListener(
        lifecycle = this.lifecycle,
        root = this.rootView,
        window = this.window,
        onKeyboardShow = onKeyboardShow,
        onKeyboardHide = onKeyboardHide,
    )

    /**
     * Gets the root view for the activity.
     */
    abstract val rootView: ViewGroup
}
