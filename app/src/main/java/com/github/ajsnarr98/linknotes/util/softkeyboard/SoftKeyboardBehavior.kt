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

    companion object {
        const val DEFAULT_DEFAULT_VISIBILITY = View.GONE
        const val DEFAULT_SCROLL_AMOUNT = 0
        val DEFAULT_EXTRA_VISIBLITY_CHECK: () -> Boolean = { true }
    }

    /** Extra amount to scroll in pixels when the soft keyboard pops up. **/
    private val scrollAmount: Int
    private val defaultVisibility: Int
    private val shouldMakeVisible: () -> Boolean
    private var hasCreatedKeyboardListener: Boolean = false

    @JvmOverloads
    constructor(
        defaultVisibility: Int = DEFAULT_DEFAULT_VISIBILITY,
        scrollAmount: Int = DEFAULT_SCROLL_AMOUNT,
        shouldMakeVisible: () -> Boolean = DEFAULT_EXTRA_VISIBLITY_CHECK,
    ) : super() {
        this.defaultVisibility = defaultVisibility
        this.scrollAmount = scrollAmount
        this.shouldMakeVisible = shouldMakeVisible
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.defaultVisibility = DEFAULT_DEFAULT_VISIBILITY
        this.scrollAmount = DEFAULT_SCROLL_AMOUNT
        this.shouldMakeVisible = DEFAULT_EXTRA_VISIBLITY_CHECK
    }

    override fun onLayoutChild(parent: CoordinatorLayout, child: V, layoutDirection: Int): Boolean {
        if (!hasCreatedKeyboardListener) {
            if (parent is SoftKeyboardAware) {
                parent.addSoftKeyboardListener { isKeyboardShown ->
                    if (shouldMakeVisible()) {
                        // only show if extra conditions for visibility are met
                        child.visibility = if (isKeyboardShown) View.VISIBLE else defaultVisibility
                        val direction = if (isKeyboardShown) 1 else -1
                        parent.scrollVerticallyBy(scrollAmount * direction)
                    }
                }
            } else {
                throw IllegalStateException("Unsupported Coordinator Layout type for SoftKeyboardBehavior")
            }
            hasCreatedKeyboardListener = true
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }
}