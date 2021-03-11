package com.github.ajsnarr98.linknotes.util.softkeyboard

import android.content.Context
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.ajsnarr.linknotes.R

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
    private val keyboardListeners: MutableSet<(isKeyboardShown: Boolean) -> Unit> = LinkedHashSet()

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

    override fun addSoftKeyboardListener(listener: (isKeyboardShown: Boolean) -> Unit): Boolean
        = keyboardListeners.add(listener)

    override fun removeSoftKeyboardListener(listener: (isKeyboardShown: Boolean) -> Unit): Boolean
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
            val isSoftKeyboardShown = actualHeight > proposedHeight
            for (listener in keyboardListeners) {
                listener(isSoftKeyboardShown)
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
}