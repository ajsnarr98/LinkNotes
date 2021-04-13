package com.github.ajsnarr98.linknotes.util.softkeyboard

import android.content.Context
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.github.ajsnarr98.linknotes.util.absoluteTopVerticalPos
import com.github.ajsnarr98.linknotes.util.addOnTextChangedListener
import com.github.ajsnarr98.linknotes.util.selectedVerticalPos
import kotlin.math.abs

/**
 * Shows a view as VISIBLE when the soft keyboard is showing, or shows a view
 * as GONE when the keyboard is gone. Default visibility can be changed from
 * GONE to INVISIBLE through the constructor.
 */
open class SoftKeyboardAccessoryBehavior<V : View> : CoordinatorLayout.Behavior<V> {

    companion object {
        const val DEFAULT_DEFAULT_VISIBILITY = View.GONE
        const val DEFAULT_SCROLL_AMOUNT = 0
        const val DEFAULT_ACCESSORY_TOP_PADDING = 0 // px
        val DEFAULT_EXTRA_VISIBLITY_CHECK: (focusedView: View) -> Boolean = { true }

        private const val DOWN = 1
        private const val UP = -1
    }

    /** Extra amount to scroll in pixels when the soft keyboard pops up. **/
    private val scrollAmount: Int
    /** Padding that is always shown between the top of the accessory view and
     * the current selection **/
    private val topPadding: Int
    private val defaultVisibility: Int
    private val shouldMakeVisible: (focusedView: View) -> Boolean
    private var parentLayout: CoordinatorLayout? = null
    private var lastFocusedView: View? = null
    private var currentLineCountListener: TextWatcher? = null

    @JvmOverloads
    constructor(
        defaultVisibility: Int = DEFAULT_DEFAULT_VISIBILITY,
        scrollAmount: Int = DEFAULT_SCROLL_AMOUNT,
        topPadding: Int = DEFAULT_ACCESSORY_TOP_PADDING,
        shouldMakeVisible: (focusedView: View) -> Boolean = DEFAULT_EXTRA_VISIBLITY_CHECK,
    ) : super() {
        this.defaultVisibility = defaultVisibility
        this.scrollAmount = scrollAmount
        this.topPadding = topPadding
        this.shouldMakeVisible = shouldMakeVisible
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        this.defaultVisibility = DEFAULT_DEFAULT_VISIBILITY
        this.scrollAmount = DEFAULT_SCROLL_AMOUNT
        this.topPadding = DEFAULT_ACCESSORY_TOP_PADDING
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
                        val direction = if (isKeyboardShown) DOWN else UP
                        parent.scrollVerticallyBy((scrollAmount + topPadding) * direction)
                    } else {
                        child.visibility = defaultVisibility
                    }
                    updateSelectionPositionListeners(isKeyboardShown, focusedView, parent, child)
                }
            } else {
                throw IllegalStateException("Unsupported Coordinator Layout type for SoftKeyboardBehavior")
            }
        }
        return super.onLayoutChild(parent, child, layoutDirection)
    }

    private fun updateSelectionPositionListeners(isKeyboardShown: Boolean, focusedView: View, parent: SoftKeyboardAware, child: V) {
        // check for line count changes, and see if bottom part of selection
        // differs from top part of accessory view (child)
        if (focusedView != lastFocusedView) {
            val lastV = lastFocusedView
            val l = currentLineCountListener
            if (lastV is EditText && l != null) {
                lastV.removeTextChangedListener(l)
            }
            if (isKeyboardShown && focusedView is EditText) {
                focusedView.addOnTextChangedListener { _: CharSequence?, _: Int ->
                    // subtract padding from child to go upward
                    val childTopPos: Int = abs(child.absoluteTopVerticalPos() - topPadding)
                    val textSelectionBottomPos: Int = focusedView.selectedVerticalPos() ?: (childTopPos - 1)
                    if (textSelectionBottomPos >= childTopPos) {
                        // selection is hidden by child, scroll down necessary amount
                        val correctionScrollAmount = textSelectionBottomPos - childTopPos
                        parent.scrollVerticallyBy(correctionScrollAmount * DOWN)
                    }
                }
            }
            lastFocusedView = focusedView
        }
    }
}