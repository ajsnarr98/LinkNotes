package com.github.ajsnarr98.linknotes.util.softkeyboard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.github.ajsnarr98.linknotes.util.getActivity
import com.github.ajsnarr98.linknotes.util.markdown.MarkdownEditText

/**
 * Shows a view as VISIBLE when the soft keyboard is showing, or shows a view
 * as GONE when the keyboard is gone. Default visibility can be changed from
 * GONE to INVISIBLE through the constructor.
 */
open class SoftKeyboardAccessoryBehavior<V : View> : CoordinatorLayout.Behavior<V> {

    companion object {
        const val DEFAULT_DEFAULT_VISIBILITY = View.GONE
        const val DEFAULT_SCROLL_AMOUNT = 0
        val DEFAULT_EXTRA_VISIBLITY_CHECK: (focusedView: View) -> Boolean = { true }
    }

    /** Extra amount to scroll in pixels when the soft keyboard pops up. **/
    private val scrollAmount: Int
    private val defaultVisibility: Int
    private val shouldMakeVisible: (focusedView: View) -> Boolean
    private var parentLayout: CoordinatorLayout? = null

    @JvmOverloads
    constructor(
        defaultVisibility: Int = DEFAULT_DEFAULT_VISIBILITY,
        scrollAmount: Int = DEFAULT_SCROLL_AMOUNT,
        shouldMakeVisible: (focusedView: View) -> Boolean = DEFAULT_EXTRA_VISIBLITY_CHECK,
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
        if (parent != this.parentLayout) {
            this.parentLayout = parent
            if (parent is SoftKeyboardAware) {
                parent.addSoftKeyboardListener { isKeyboardShown: Boolean, focusedView: View ->
                    if (shouldMakeVisible(focusedView)) {
                        // only show if extra conditions for visibility are met
                        child.visibility = if (isKeyboardShown) View.VISIBLE else defaultVisibility
                        val direction = if (isKeyboardShown) 1 else -1
                        parent.scrollVerticallyBy(scrollAmount * direction)
                    } else {
                        child.visibility = defaultVisibility
                    }
                }
            } else {
                throw IllegalStateException("Unsupported Coordinator Layout type for SoftKeyboardBehavior")
            }
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }
}