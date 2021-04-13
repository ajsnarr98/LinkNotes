package com.github.ajsnarr98.linknotes.util

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children

/**
 * Gets the absolute top position of this view in the window.
 */
fun View.absoluteTopVerticalPos(): Int {
    val absPos = IntArray(2)
    this.getLocationInWindow(absPos)
    return absPos[1]
}

/**
 * Called when any child of this viewGroup changes focus.
 */
fun ViewGroup.setOnChildFocusChangeListener(listener: (v: View, hasFocus: Boolean) -> Unit) {
    for (child in this.children) {
        child.setOnFocusChangeListener(listener)
        if (child is ViewGroup) {
            child.setOnChildFocusChangeListener(listener)
        }
    }
}
