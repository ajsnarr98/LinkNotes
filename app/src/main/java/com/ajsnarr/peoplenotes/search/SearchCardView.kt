package com.ajsnarr.peoplenotes.search

import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import androidx.cardview.widget.CardView

/**
 * Allows back button presses to be dispatched even while the keyboard is open.
 */
class SearchCardView : CardView {

    private lateinit var mSearchActivity: Activity

    var searchActivity
        get() = mSearchActivity
        set(value) { mSearchActivity = value }

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
        if (mSearchActivity != null && event.getKeyCode() === KeyEvent.KEYCODE_BACK) {
            val state = keyDispatcherState
            if (state != null) {
                if (event.getAction() === KeyEvent.ACTION_DOWN && event.getRepeatCount() === 0) {
                    state.startTracking(event, this)
                    return true
                } else if (event.getAction() === KeyEvent.ACTION_UP
                    && !event.isCanceled() && state.isTracking(event)
                ) {
                    mSearchActivity.onBackPressed()
                    return true
                }
            }
        }
        return super.dispatchKeyEventPreIme(event)
    }
}
