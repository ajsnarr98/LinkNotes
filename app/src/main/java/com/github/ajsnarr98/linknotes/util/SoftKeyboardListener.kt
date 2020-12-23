package com.github.ajsnarr98.linknotes.util

import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.view.Window
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner


/**
 * A listener for the when the soft keyboard is shown/not shown. Destroyed at
 * the end of the given lifecycle.
 *
 * NOTE: this only works when android:windowSoftInputMode of the activity is
 *       set to adjustResize.
 *
 * Based on https://stackoverflow.com/questions/25216749/soft-keyboard-open-and-close-listener-in-an-activity-in-android
 */
class SoftKeyboardListener(
    lifecycle: Lifecycle,
    private val root: ViewGroup,
    window: Window,
    var onKeyboardShow: () -> Unit = {},
    var onKeyboardHide: () -> Unit = {},
) : DefaultLifecycleObserver {

    private val keyboardLayoutListener = OnGlobalLayoutListener {
        val heightDiff: Int = root.rootView.height - root.height
        val contentViewTop: Int = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
        if (heightDiff <= contentViewTop) {
            onKeyboardHide()
        } else {
            onKeyboardShow()
        }
    }

    init {
        lifecycle.addObserver(this)
        root.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)

    }

    override fun onDestroy(owner: LifecycleOwner) {
        root.viewTreeObserver.removeOnGlobalLayoutListener(keyboardLayoutListener)
    }
}