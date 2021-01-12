package com.github.ajsnarr98.linknotes.util.softkeyboard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * Shows a view as VISIBLE when the soft keyboard is showing, or shows a view
 * as GONE when the keyboard is gone. Default visibility can be changed from
 * GONE to INVISIBLE through the constructor.
 */
open class SoftKeyboardBehavior<V : View> : CoordinatorLayout.Behavior<V> {
    constructor() : super()
    constructor(defaultVisibility: Int) : super() { this.defaultVisibility = defaultVisibility }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    private var defaultVisibility = View.GONE
    private var hasCreatedKeyboardListener: Boolean = false

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        if (!hasCreatedKeyboardListener) {
            if (parent is SoftKeyboardListenerHolder) {
                parent.addSoftKeyboardListener { isKeyboardShown ->
                    child.visibility = if (isKeyboardShown) View.VISIBLE else defaultVisibility
                }
            } else {
                throw IllegalStateException("Unsupported Coordinator Layout type for SoftKeyboardBehavior")
            }
            hasCreatedKeyboardListener = true
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }
}