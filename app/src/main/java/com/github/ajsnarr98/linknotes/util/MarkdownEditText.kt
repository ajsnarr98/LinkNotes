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
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    companion object {
        const val ITALICS_MARKER = "*"
        const val BOLD_MARKER = "**"
        const val BOLD_ITALICS_MARKER = "***"

        /**
         * First finds whether or not the selection between start and end
         * is exactly between two (paired) instances of the given marker, or
         * has instances of the given marker on exactly either end, or if there
         * are pairs of markers inside the selection.
         *
         * Returns indices of all relevant markers that should be removed.
         */
        fun markersToRemove(text: String, marker: String, start: Int, end: Int): List<Int> {
            val actualStart = if (start > end) end else start
            val actualEnd = if (start > end) start else end
            val allMarkers = mutableListOf<Int>()
            val markersBeforeStart = mutableListOf<Int>()
            val markersInSelection = mutableListOf<Int>()
            val markersAfterEnd = mutableListOf<Int>()
            var i: Int = text.indexOf(marker)
            while (i >= 0) {
                if (i <= actualStart) {
                    markersBeforeStart.add(i)
                } else if (i >= actualEnd) {
                    markersAfterEnd.add(i)
                }
                if (i in actualStart..actualEnd) {
                    markersInSelection.add(i)
                }
                allMarkers.add(i)
                i = text.indexOf(marker, i + 1)
            }

            val isSelectionInMarker =
                (markersBeforeStart.size % 2 == 1 && markersAfterEnd.size % 2 == 1)
                        || (markersInSelection.size % 2 == 0)
            if (!isSelectionInMarker) {
                return emptyList()
            }

            val toRemove = mutableSetOf<Int>()
            toRemove.addAll(markersInSelection)
            if (markersBeforeStart.size % 2 == 1) {
                toRemove.add(markersBeforeStart.last())
            }
            if (toRemove.size % 2 == 1 && markersAfterEnd.size > 0) {
                toRemove.add(markersAfterEnd.first())
            }
            return toRemove.sorted()
        }

        /**
         * Un-marks text.
         *
         * @return modified text
         */
        fun getUnMarkedText(text: String, toRemove: String, start: Int, end: Int): String {
            return getUnMarkedText(
                text,
                toRemove,
                start,
                end,
                markersToRemove(text, toRemove, start, end)
            )
        }

        /**
         * Un-marks text within selection. If start and end are equal (cursor is
         * just clicked somewhere) remove everything. Else, if one or more
         * characters are selected, remove only within selection, and fix on
         * either end.
         *
         * @param text text to un-mark
         * @param toRemove marker being removed
         * @param start start of selection
         * @param end of selection
         * @param removeIndices indices of markers being removed. Get this from markersToRemove()
         *
         * @return modified text
         */
        fun getUnMarkedText(
            text: String,
            toRemove: String,
            start: Int,
            end: Int,
            removeIndices: Collection<Int>
        ): String {
            val sortedIndices = removeIndices.sorted()
            val actualStart = if (start > end) end else start
            val actualEnd = if (start > end) start else end
            val removeAll = start == end

            var result = ""
            var i = 0
            for (j in sortedIndices) {
                if (removeAll || j in actualStart..actualEnd) {
                    result += text.substring(i, j)
                    i = j + toRemove.length
                } else if (j < actualStart) {
                    // when not removing all, and there is the start of a pair
                    // outside the selection, we must complete that pair
                    result += text.substring(i, actualStart) + toRemove
                    i = actualStart
                } else if (j > actualEnd) {
                    result += text.substring(i, actualEnd) + toRemove
                    i = actualEnd
                }
            }
            if (i < text.length) {
                result += text.substring(i)
            }
            return result
        }

        /**
         * Adds the given string to the given text before start and after end.
         */
        fun getMarkedText(text: String, toAdd: String, start: Int, end: Int): String {
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
         * @param marker string that was added
         * @param oldStart old selection start before text was modified
         * @param oldEnd old selection start before text was modified
         *
         * @return pair of (newStart, newEnd) for selection
         */
        fun getNewSelectionOnMark(
            marker: String,
            oldStart: Int,
            oldEnd: Int
        ): Pair<Int, Int> {
            val actualStart = if (oldStart > oldEnd) oldEnd else oldStart
            val actualEnd = if (oldStart > oldEnd) oldStart else oldEnd
            return Pair(actualStart + marker.length, actualEnd + marker.length)
        }

        /**
         * Calculates the new start and end of the selection in the newly modified
         * text, given the string that was removed from the old text.
         *
         * @param marker string that was added
         * @param oldStart old selection start before text was modified
         * @param oldEnd old selection start before text was modified
         * @param removedFrom collection of indices passed into getUnMarkedText()
         *
         * @return pair of (newStart, newEnd) for selection
         */
        fun getNewSelectionOnUnMark(
            marker: String,
            oldStart: Int,
            oldEnd: Int,
            removedFrom: Collection<Int>
        ): Pair<Int, Int> {
            val sortedIndices = removedFrom.sorted()
            val actualStart = if (oldStart > oldEnd) oldEnd else oldStart
            val actualEnd = if (oldStart > oldEnd) oldStart else oldEnd
            val removedAll = oldStart == oldEnd

            val newStart: Int
            val newEnd: Int
            if (removedAll) {
                // return the beginning of removed selection and end of removed selection
                newStart = sortedIndices.first()
                newEnd = sortedIndices.last() - (sortedIndices.size * marker.length)
            } else {
                // return the normal modified selection
                newStart = if (sortedIndices.first() < actualStart) {
                    actualStart + marker.length
                } else {
                    actualStart
                }
                newEnd = if (sortedIndices.first() < actualStart) {
                    if (sortedIndices.last() > actualEnd) {
                        actualEnd - ((sortedIndices.size - 2) * marker.length)
                    } else {
                        actualEnd - ((sortedIndices.size - 1) * marker.length)
                    }
                } else {
                    actualEnd - (sortedIndices.size * marker.length)
                }
            }
            return Pair(newStart, newEnd)
        }
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
        val newSelection = getNewSelectionOnMark(BOLD_MARKER, start, end)
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