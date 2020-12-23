package com.github.ajsnarr98.linknotes.util

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
            parent.context.getActivityOrNull()?.createKeyboardListener(
                onKeyboardShow = { child.visibility = View.VISIBLE },
                onKeyboardHide = { child.visibility = View.GONE },
            )
            hasCreatedKeyboardListener = true
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }
}