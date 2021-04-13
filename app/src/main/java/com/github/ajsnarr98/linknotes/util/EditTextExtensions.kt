package com.github.ajsnarr98.linknotes.util

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Gets lowest vertical position (absolute coordinates) of the selected text.
 * This EditText should be in focus.
 */
fun EditText.selectedVerticalPos(): Int? {
    layout ?: return null // Layout may be null right after change to the text view

    val charPos = max(selectionEnd, selectionStart)
    val lineOfText = layout.getLineForOffset(charPos)
    return layout.getLineBottom(lineOfText) + this.absoluteTopVerticalPos()
}

/**
 * Adds a listener for when the number of lines in the given editText changes.
 *
 * @return the listener that was created
 */
fun EditText.addOnTextChangedListener(onTextChange: (text: CharSequence?, numLines: Int) -> Unit): TextWatcher {
    val watcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable?) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            val curLineCount = this@addOnTextChangedListener.lineCount
            onTextChange(s, curLineCount)
        }
    }
    this.addTextChangedListener(watcher)
    return watcher
}
