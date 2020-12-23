package com.github.ajsnarr98.linknotes.util.softkeyboard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

open class SoftKeyboardBehavior<V : View> : CoordinatorLayout.Behavior<V> {
    constructor() : super()
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var hasCreatedKeyboardListener: Boolean = false

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        if (!hasCreatedKeyboardListener) {
            if (parent is SoftKeyboardListenerHolder) {
                parent.addSoftKeyboardListener { isKeyboardShown ->
                    child.visibility = if (isKeyboardShown) View.VISIBLE else View.GONE
                }
            } else {
                throw IllegalStateException("Unsupported Coordinator Layout type for SoftKeyboardBehavior")
            }
            hasCreatedKeyboardListener = true
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }
}