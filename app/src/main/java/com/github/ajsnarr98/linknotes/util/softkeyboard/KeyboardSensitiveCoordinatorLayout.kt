package com.github.ajsnarr98.linknotes.util.softkeyboard

import android.content.Context
import android.util.AttributeSet
import androidx.coordinatorlayout.widget.CoordinatorLayout

/**
 * NOTE: this layout must exist in an activity where softInputMode is "adjustResize"
 */
class KeyboardSensitiveCoordinatorLayout : CoordinatorLayout, SoftKeyboardListenerHolder {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val keyboardListeners: MutableSet<(isKeyboardShown: Boolean) -> Unit> = LinkedHashSet()

    override fun addSoftKeyboardListener(listener: (isKeyboardShown: Boolean) -> Unit): Boolean
        = keyboardListeners.add(listener)

    override fun removeSoftKeyboardListener(listener: (isKeyboardShown: Boolean) -> Unit): Boolean
        = keyboardListeners.remove(listener)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
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