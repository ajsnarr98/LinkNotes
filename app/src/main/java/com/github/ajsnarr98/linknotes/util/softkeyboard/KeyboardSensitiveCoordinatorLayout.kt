package com.github.ajsnarr98.linknotes.util.softkeyboard

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.github.ajsnarr98.linknotes.R
import com.github.ajsnarr98.linknotes.util.getActivity
import com.github.ajsnarr98.linknotes.util.setOnChildFocusChangeListener
import timber.log.Timber

/**
 * NOTE: this layout must exist in an activity where softInputMode is "adjustResize"
 */
class KeyboardSensitiveCoordinatorLayout : CoordinatorLayout, SoftKeyboardAware {
    constructor(context: Context) : super(context) { recyclerViewId = null }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        recyclerViewId = getRecyclerViewId(attrs)
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        recyclerViewId = getRecyclerViewId(attrs)
    }

    private val recyclerViewId: Int?
    private var recyclerView: RecyclerView? = null
    private val keyboardListeners: MutableSet<(isKeyboardShown: Boolean, focusedView: View) -> Unit> = LinkedHashSet()
    private var oldFocusedView: View? = null
    private var isSoftKeyboardShown: Boolean = false

    private val childFocusChangedListener = { _: View, hasFocus: Boolean ->
        if (hasFocus && this.isSoftKeyboardShown) {
            // report keyboard has been shown when a new view gets focus
            reportKeyboardShown(this.isSoftKeyboardShown)
        }
    }

    private fun getRecyclerViewId(attrs: AttributeSet): Int? {
        var ret: Int? = null
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.KeyboardSensitiveCoordinatorLayout,
            0, 0).apply {

            try {
                ret = getResourceId(R.styleable.KeyboardSensitiveCoordinatorLayout_recyclerView, 0)
                if (ret == 0) {
                    ret = null
                }
            } finally {
                recycle()
            }
        }
        return ret
    }

    override fun addSoftKeyboardListener(listener: (isKeyboardShown: Boolean, focusedView: View) -> Unit): Boolean
        = keyboardListeners.add(listener)

    override fun removeSoftKeyboardListener(listener: (isKeyboardShown: Boolean, focusedView: View) -> Unit): Boolean
        = keyboardListeners.remove(listener)

    override fun scrollVerticallyBy(dy: Int) {
        recyclerView?.smoothScrollBy(0, dy)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (recyclerView == null && recyclerViewId != null) {
            // find recycler view
            recyclerView = findViewById(recyclerViewId)
        }
        val proposedHeight = MeasureSpec.getSize(heightMeasureSpec)
        val actualHeight = height
        if (actualHeight != proposedHeight && keyboardListeners.isNotEmpty()) {
            this.isSoftKeyboardShown = actualHeight > proposedHeight
            setOnChildFocusChangeListener(childFocusChangedListener)
            reportKeyboardShown(isSoftKeyboardShown)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    /**
     * Called when the keyboard is shown, disappears, or when the focus
     * changes while the keyboard is still being shown.
     */
    private fun reportKeyboardShown(isSoftKeyboardShown: Boolean) {
        val focusedView: View? = getActivity(this.context)?.currentFocus
        if (focusedView != null && focusedView == oldFocusedView) {
            Timber.w("Old focused view (for keyboard accessory) is the same as current")
        }
        for (listener in keyboardListeners) {
            listener(isSoftKeyboardShown, focusedView ?: this)
        }
    }
}