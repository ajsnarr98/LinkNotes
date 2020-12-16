package com.github.ajsnarr98.linknotes.search

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.cardview.widget.CardView

/**
 * Allows back button presses to be dispatched even while the keyboard is open.
 */
class SearchCardView : CardView {

    lateinit var activity: Activity

    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet?, int: Int) : super(context, attributeSet, int)

    /**
     * Overrides the handling of the back key to move back to the
     * previous sources or dismiss the search dialog, instead of
     * dismissing the input method.
     *
     * source: https://stackoverflow.com/questions/3940127/intercept-back-button-from-soft-keyboard
     */
    override fun dispatchKeyEventPreIme(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            val state = keyDispatcherState
            if (state != null) {
                if (event.action == KeyEvent.ACTION_DOWN && event.repeatCount == 0) {
                    state.startTracking(event, this)
                    return true
                } else if (event.action == KeyEvent.ACTION_UP
                    && !event.isCanceled && state.isTracking(event)
                ) {
                    activity.onBackPressed()
                    return true
                }
            }
        }
        return super.dispatchKeyEventPreIme(event)
    }
}
