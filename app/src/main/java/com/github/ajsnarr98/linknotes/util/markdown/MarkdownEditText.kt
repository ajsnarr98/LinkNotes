package com.github.ajsnarr98.linknotes.util.markdown

import android.content.Context
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import timber.log.Timber
import kotlin.math.abs

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

    init {
        // add listener on edits to continue a list if the previous line was
        // part of a list
        addTextChangedListener(object : TextWatcher {
            private var mListContinuationInfo = Markdown.MarkdownListContinuation(
                deletePrevious = false,
                listMarker = "",
                listMarkerPos = 0,
                listMarkerLineStart = 0,
                indent = 0,
            )
            private var mWasNewlineAdded = false
            private var mNewlinePos = -1

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                try {
                    mListContinuationInfo = Markdown.getSelectionContinuesList(
                        this@MarkdownEditText.text.toString(),
                        this@MarkdownEditText.selectionStart,
                        this@MarkdownEditText.selectionEnd,
                    )
                } catch (e: Exception) {
                    Timber.e(e)
                    throw e
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                mNewlinePos = s?.substring(0, start + count)?.lastIndexOf('\n') ?: -1
                mWasNewlineAdded = s != null && '\n' in s.substring(start, start + count)
            }

            override fun afterTextChanged(s: Editable?) {
                try {
                    // get local copies in case these values change
                    val newlinePos = this.mNewlinePos
                    val wasNewlineAdded = this.mWasNewlineAdded
                    val listContinuationInfo = this.mListContinuationInfo
                    if (s != null && wasNewlineAdded && listContinuationInfo.listMarker.isNotEmpty()) {
                        if (listContinuationInfo.deletePrevious) {
                            val oldText = s.toString()
                            var newText = ""
                            val listMarkerFullIndLength = listContinuationInfo.indent + listContinuationInfo.listMarker.length
                            // delete content of previous line and don't continue list
                            var pos = listContinuationInfo.listMarkerLineStart
                            newText += oldText.substring(0, pos)
                            pos += listMarkerFullIndLength
                            // also remove new newline
                            newText += oldText.substring(pos, newlinePos)
                            pos += 1
                            newText += oldText.substring(pos)

                            val start =
                                if (selectionStart <= selectionEnd) selectionStart else selectionEnd
                            val end =
                                if (selectionStart <= selectionEnd) selectionEnd else selectionStart
                            setText(newText)


                            val selectionDiff = -(1 + listMarkerFullIndLength)
                            Selection.setSelection(
                                this@MarkdownEditText.text,
                                start + selectionDiff,
                                end + selectionDiff,
                            )
                        } else {
                            // continue list on selected lines
                            val modifiedText = Markdown.addListMarker(
                                s.toString(),
                                listContinuationInfo.listMarker,
                                listContinuationInfo.indent,
                                selectionStart,
                                selectionEnd,
                            )
                            // make sure markers were added and not removed
                            if (modifiedText.newText.length >= s.toString().length) {
                                setText(modifiedText.newText)
                                Selection.setSelection(
                                    this@MarkdownEditText.text,
                                    modifiedText.newSelectionStart,
                                    modifiedText.newSelectionEnd,
                                )
                            }
                        }
                    }
                    this.mWasNewlineAdded = false
                    this.mListContinuationInfo = Markdown.MarkdownListContinuation(
                        deletePrevious = false,
                        listMarker = "",
                        listMarkerPos = 0,
                        listMarkerLineStart = 0,
                        indent = 0,
                    )
                } catch (e: Exception) {
                    Timber.e(e)
                    throw e
                }
            }
        })
    }

    /**
     * Indents a list forward if applicable to the current selection.
     */
    fun addListIndent() {
        val modifiedText = Markdown.addListIndent(this.text.toString(), selectionStart, selectionEnd)
        this.setText(modifiedText.newText)
        Selection.setSelection(this.text, modifiedText.newSelectionStart, modifiedText.newSelectionEnd)
    }

    /**
     * Adds a bullet list, removes the existing bullet list, or replaces an
     * existing numbered list with a bullet list.
     */
    fun toggleBulletList() {
        val modifiedText = Markdown.toggleListMarker(this.text.toString(), Markdown.BULLET_LIST_MARKER, selectionStart, selectionEnd)
        this.setText(modifiedText.newText)
        Selection.setSelection(this.text, modifiedText.newSelectionStart, modifiedText.newSelectionEnd)
    }

    /**
     * Adds a numbered list, removes the existing numbered list, or replaces an
     * existing bullet list with a numbered list.
     */
    fun toggleNumberedList() {
        val modifiedText = Markdown.toggleListMarker(this.text.toString(), Markdown.NUMBERED_LIST_MARKER, selectionStart, selectionEnd)
        this.setText(modifiedText.newText)
        Selection.setSelection(this.text, modifiedText.newSelectionStart, modifiedText.newSelectionEnd)
    }

    fun toggleBold() {
        val modifiedText = Markdown.toggleSurroundingMarker(this.text.toString(), Markdown.BOLD_MARKER, selectionStart, selectionEnd)
        this.setText(modifiedText.newText)
        Selection.setSelection(this.text, modifiedText.newSelectionStart, modifiedText.newSelectionEnd)
    }

    fun toggleItalic() {
        val modifiedText = Markdown.toggleSurroundingMarker(this.text.toString(), Markdown.ITALICS_MARKER, selectionStart, selectionEnd)
        this.setText(modifiedText.newText)
        Selection.setSelection(this.text, modifiedText.newSelectionStart, modifiedText.newSelectionEnd)
    }

    fun toggleUnderline() {

    }

    fun toggleStrikethrough() {
        val modifiedText = Markdown.toggleSurroundingMarker(this.text.toString(), Markdown.STRIKE_THROUGH_MARKER, selectionStart, selectionEnd)
        this.setText(modifiedText.newText)
        Selection.setSelection(this.text, modifiedText.newSelectionStart, modifiedText.newSelectionEnd)
    }

    fun addLink() {

    }

    fun addPhoto() {

    }
}