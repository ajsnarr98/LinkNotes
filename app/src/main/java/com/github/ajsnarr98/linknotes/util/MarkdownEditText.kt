package com.github.ajsnarr98.linknotes.util

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
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        const val BOLD_MARKER = "*"
        const val ITALICS_MARKER = "**"
    }

    /**
     * Adds the given string to the given text before start and after end.
     */
    private fun getMarkedText(text: String, toAdd: String, start: Int, end: Int): String {
        val actualStart = if (start > end) end else start
        val actualEnd = if (start > end) start else end
        return if (actualStart == text.length) {
            text + toAdd + toAdd
        } else if (actualEnd == text.length) {
            text.substring(0, actualStart) +
                    toAdd +
                    text.substring(actualStart, actualEnd) +
                    toAdd
        } else {
            text.substring(0, actualStart) +
                    toAdd +
                    text.substring(actualStart, actualEnd) +
                    toAdd +
                    text.substring(actualEnd, text.length)
        }
    }

    /**
     * Calculates the new start and end of the selection in the newly modified
     * text, given the string that was added to the old text.
     *
     * @param newText newly modified text
     * @param toAdd string that was added
     * @param oldStart old selection start before text was modified
     * @param oldEnd old selection start before text was modified
     *
     * @return pair of (newStart, newEnd) for selection
     */
    private fun getNewSelection(newText: String, toAdd: String, oldStart: Int, oldEnd: Int): Pair<Int, Int> {
        val actualStart = if (oldStart > oldEnd) oldEnd else oldStart
        val actualEnd = if (oldStart > oldEnd) oldStart else oldEnd
        return Pair(actualStart + toAdd.length, actualEnd + toAdd.length)
    }

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
        val start = selectionStart
        val end = selectionEnd
        var text = this.text.toString()
        text = getMarkedText(text, BOLD_MARKER, start, end)
        val newSelection = getNewSelection(text, BOLD_MARKER, start, end)
        setText(text)
        Selection.setSelection(this.text, newSelection.first, newSelection.second)
    }

    fun toggleItalic() {

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