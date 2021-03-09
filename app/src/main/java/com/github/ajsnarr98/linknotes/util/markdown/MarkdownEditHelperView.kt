package com.github.ajsnarr98.linknotes.util.markdown

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.ajsnarr.linknotes.R
import com.ajsnarr.linknotes.databinding.ViewMarkdownEditHelperBinding
import com.github.ajsnarr98.linknotes.util.getActivity
import com.github.ajsnarr98.linknotes.util.softkeyboard.SoftKeyboardBehavior
import kotlin.math.roundToInt

class MarkdownEditHelperView : ConstraintLayout, CoordinatorLayout.AttachedBehavior {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private val actionListener: ActionListener = ActionListener(context)
    private val binding: ViewMarkdownEditHelperBinding
        = ViewMarkdownEditHelperBinding.bind(inflate(context, R.layout.view_markdown_edit_helper, this)).apply {

        bulletList.setOnClickListener { actionListener.onBulletList() }
        numberedList.setOnClickListener { actionListener.onNumberedList() }
        bold.setOnClickListener { actionListener.onBold() }
        italic.setOnClickListener { actionListener.onItalic() }
        underline.setOnClickListener { actionListener.onUnderline() }
        strikethrough.setOnClickListener { actionListener.onStrikethrough() }
        header.setOnClickListener { actionListener.onHeader() }
        addLink.setOnClickListener { actionListener.onLink() }
        addPhoto.setOnClickListener { actionListener.onPhoto() }
    }

    override fun getBehavior(): CoordinatorLayout.Behavior<*>
        = SoftKeyboardBehavior<MarkdownEditHelperView>(
            defaultVisibility = View.INVISIBLE,
            scrollAmount = context.resources.getDimension(R.dimen.markdown_edit_helper_height).roundToInt(),
            // make sure only show as visible if the selected view supports markdown edit
            shouldMakeVisible = { actionListener.focusedField != null },
        )

    class ActionListener(val context: Context) {
        val focusedField: MarkdownEditText?
            get() = getActivity(context)?.currentFocus as? MarkdownEditText

        fun onBulletList() = focusedField?.toggleBulletList()
        fun onNumberedList() = focusedField?.toggleNumberedList()
        fun onBold() = focusedField?.toggleBold()
        fun onItalic() = focusedField?.toggleItalic()
        fun onUnderline() = focusedField?.toggleUnderline()
        fun onStrikethrough() = focusedField?.toggleStrikethrough()
        fun onHeader() = focusedField?.addHeader()
        fun onLink() = focusedField?.addLink()
        fun onPhoto() = focusedField?.addPhoto()
    }
}