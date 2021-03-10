package com.github.ajsnarr98.linknotes.util.markdown

import android.content.Context
import android.text.Selection
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText

/**
 * An EditText that has a few helpers that help markdown editing.
 */
class MarkdownEditText : AppCompatEditText {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private fun hasBulletList() {

    }

    /**
     * Adds a bullet list, removes the existing bullet list, or replaces an
     * existing numbered list with a bullet list.
     */
    fun toggleBulletList() {

    }

    /**
     * Adds a numbered list, removes the existing numbered list, or replaces an
     * existing bullet list with a numbered list.
     */
    fun toggleNumberedList() {

    }

    fun toggleBold() {
        val modifiedText = Markdown.toggleMarker(this.text.toString(), Markdown.BOLD_MARKER, selectionStart, selectionEnd)
        this.setText(modifiedText.newText)
        Selection.setSelection(this.text, modifiedText.newSelectionStart, modifiedText.newSelectionEnd)
    }

    fun toggleItalic() {
        val modifiedText = Markdown.toggleMarker(this.text.toString(), Markdown.ITALICS_MARKER, selectionStart, selectionEnd)
        this.setText(modifiedText.newText)
        Selection.setSelection(this.text, modifiedText.newSelectionStart, modifiedText.newSelectionEnd)
    }

    fun toggleUnderline() {

    }

    fun toggleStrikethrough() {

    }

    fun addHeader() {

    }

    fun addLink() {

    }

    fun addPhoto() {

    }
}