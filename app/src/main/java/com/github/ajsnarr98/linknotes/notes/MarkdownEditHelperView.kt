package com.github.ajsnarr98.linknotes.notes

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.ajsnarr.linknotes.R
import com.ajsnarr.linknotes.databinding.ViewMarkdownEditHelperBinding
import com.github.ajsnarr98.linknotes.util.softkeyboard.SoftKeyboardBehavior

class MarkdownEditHelperView : ConstraintLayout, CoordinatorLayout.AttachedBehavior {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    interface ActionListener {
    }

    var actionListener: ActionListener
        get() = _actionListener ?: throw IllegalStateException("Action listener was never set")
        set(value) { _actionListener = value }

    private var _actionListener: ActionListener? = null
    private val binding: ViewMarkdownEditHelperBinding
        = ViewMarkdownEditHelperBinding.bind(inflate(context, R.layout.view_markdown_edit_helper, this)).apply {

    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*>
        = SoftKeyboardBehavior<MarkdownEditHelperView>(defaultVisibility = View.INVISIBLE)
}