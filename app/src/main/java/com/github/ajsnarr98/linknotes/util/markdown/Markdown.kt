package com.github.ajsnarr98.linknotes.util.markdown

/**
 * A collection of helper functions for markdown editing.
 */
object Markdown {
    const val ITALICS_MARKER = "_"
    const val BOLD_MARKER = "**"
    const val STRIKE_THROUGH_MARKER = "~~"

    const val BULLET_LIST_MARKER = "- "
    const val NUMBERED_LIST_MARKER = "1. "

    private const val LIST_INDENT = 4 // num spaces

    /**
     * All markers for list items.
     */
    private val LIST_MARKERS = listOf(
        BULLET_LIST_MARKER,
        NUMBERED_LIST_MARKER,
    )

    /**
     * Mapping of markers (value) that must be accounted for when checking
     * for a given marker (key).
     */
    private val relevantMarkersMap = mapOf<String, List<String>>(
        ITALICS_MARKER to listOf(BOLD_MARKER, STRIKE_THROUGH_MARKER),
        BOLD_MARKER    to listOf(ITALICS_MARKER, STRIKE_THROUGH_MARKER),
        STRIKE_THROUGH_MARKER to listOf(BOLD_MARKER, ITALICS_MARKER),
    ).withDefault { listOf() }

    data class MarkdownResult(
        val newText: String,
        val newSelectionStart: Int,
        val newSelectionEnd: Int,
    )

    fun toggleSurroundingMarker(text: String, marker: String, start: Int, end: Int): MarkdownResult {
        val markersToRemove = surroundingMarkersToRemove(text, marker, start, end)
        val newText = if (markersToRemove.isEmpty()) {
            getSurroundingMarkedText(text, marker, start, end)
        } else {
            getSurroundingUnMarkedText(text, marker, start, end, markersToRemove)
        }
        var newSelection = if (markersToRemove.isEmpty()) {
            getNewSelectionOnSurroundingMark(marker, start, end)
        } else {
            getNewSelectionOnSurroundingUnMark(text, marker, start, end, markersToRemove)
        }
        return MarkdownResult(newText, newSelection.first, newSelection.second)
    }

    /**
     * Toggles the given list marker on lines within the given selection.
     *
     * <ul>
     * <li>If there are no list markers, marks all lines.</li>
     * <li>If there are only list markers of the given type (given marker), then
     *     un-mark all of them, removing indentations</li>
     * <li>If there are list markers of different types, convert every existing
     *     list marker to the given list marker</li>
     * </ul>
     */
    fun toggleListMarker(text: String, marker: String, start: Int, end: Int): MarkdownResult {
        val listInfo: ListInfo = getLinesWithList(text, marker, start, end)
        val hasListMarkers: Boolean = listInfo.linesWithLists.any()
        return if (hasListMarkers) {
            // check if there are different list markers in selection
            val hasDifferentListMarkers = listInfo.linesWithLists == listInfo.linesWithGivenListType
            if (hasDifferentListMarkers) {
                // convert all selected list markers to the given list marker
                convertOtherListMarkersToGivenMarker(text, listInfo, start, end)
            } else {
                // un-mark all list markers
                unMarkListMarkers(text, listInfo, start, end)
            }
        } else {
            // mark all lines
            markListMarkers(text, listInfo, start, end)
        }
    }

    /**
     * First finds whether or not the selection between start and end
     * is exactly between two (paired) instances of the given marker, or
     * has instances of the given marker on exactly either end, or if there
     * are pairs of markers inside the selection.
     *
     * Returns indices of all relevant markers that should be removed.
     */
    fun surroundingMarkersToRemove(text: String, marker: String, start: Int, end: Int): List<Int> {
        val actualStart = if (start > end) end else start
        val actualEnd = if (start > end) start else end
        val allMarkers = mutableListOf<Int>()
        val markersBeforeStart = mutableListOf<Int>()
        val markersInSelection = mutableListOf<Int>()
        val markersAfterEnd = mutableListOf<Int>()
        var i: Int = text.indexOf(marker)
        while (i >= 0) {
            if (if (start == end) i < actualStart else i <= actualStart) {
                markersBeforeStart.add(i)
            } else if (i >= actualEnd) {
                markersAfterEnd.add(i)
            }
            if (i in actualStart..actualEnd) {
                markersInSelection.add(i)
            }
            allMarkers.add(i)
            i = text.indexOf(marker, i + marker.length)
        }

        val oddOrNoneBeforeStart = markersBeforeStart.size % 2 == 1 || markersBeforeStart.isEmpty()
        val oddOrNoneAfterEnd = markersAfterEnd.size % 2 == 1 || markersAfterEnd.isEmpty()
        val isSelectionInMarker =
            (oddOrNoneBeforeStart && oddOrNoneAfterEnd && !(markersBeforeStart.isEmpty() && markersAfterEnd.isEmpty()))
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
     * Unless the range is invalid, returns true if the given range (start inclusive, end exclusive) has
     * exactly 0 or 1 instances of each of the given markers, and no
     * other strings. A range is invalid if rangeEnd <= rangeStart
     */
    fun rangeContainsOnlyMarkers(text: String, rangeStart: Int, rangeEnd: Int, markers: Collection<String>): Boolean {
        if (rangeEnd <= rangeStart) {
            return false
        }
        var shrunkText = text.substring(rangeStart, rangeEnd)
        var i: Int
        for (marker in markers) {
            i = shrunkText.indexOf(marker)
            if (i >= 0) {
                // if found, remove that marker from shrunkText
                shrunkText = shrunkText.substring(0, i) + shrunkText.substring(i + marker.length)
            }
        }

        return shrunkText.isEmpty()
    }

    /**
     * For surrounding-type markers. Un-marks text.
     *
     * @return modified text
     */
    fun getSurroundingUnMarkedText(text: String, toRemove: String, start: Int, end: Int): String {
        return getSurroundingUnMarkedText(
            text,
            toRemove,
            start,
            end,
            surroundingMarkersToRemove(text, toRemove, start, end)
        )
    }

    /**
     * For surrounding-type markers.
     *
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
    fun getSurroundingUnMarkedText(
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
                if (actualStart - j <= toRemove.length) {
                    // j is touching actual start, so include it with removal
                    result += text.substring(i, j)
                    i = j + toRemove.length
                } else {
                    val isTouchingThroughOtherMarkers
                        = rangeContainsOnlyMarkers(text, j + toRemove.length, actualStart, relevantMarkersMap[toRemove] ?: listOf())
                    if (isTouchingThroughOtherMarkers) {
                        // j is touching start through other markers, so include with removal
                        result += text.substring(i, j)
                        i = j + toRemove.length
                    } else {
                        // when not removing all, and there is the start of a pair
                        // outside the selection, we must complete that pair
                        result += text.substring(i, actualStart) + toRemove
                        i = actualStart
                    }
                }
            } else if (j > actualEnd) {
                val isTouchingThroughOtherMarkers
                        = rangeContainsOnlyMarkers(text, actualEnd, j, relevantMarkersMap[toRemove] ?: listOf())
                if (isTouchingThroughOtherMarkers) {
                    // j is touching end through other markers, so include with removal
                    result += text.substring(i, j)
                    i = j + toRemove.length
                } else {
                    // when not removing all, and there is the end of a pair
                    // outside the selection, we must complete that pair
                    result += text.substring(i, actualEnd) + toRemove
                    i = actualEnd
                }
            }
        }
        if (i < text.length) {
            // add the rest of the string
            result += text.substring(i)
        }
        return result
    }

    /**
     * Adds the given string to the given text before start and after end.
     */
    fun getSurroundingMarkedText(text: String, toAdd: String, start: Int, end: Int): String {
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
    fun getNewSelectionOnSurroundingMark(
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
    fun getNewSelectionOnSurroundingUnMark(
        oldText: String,
        marker: String,
        oldStart: Int,
        oldEnd: Int,
        removedFrom: Collection<Int>
    ): Pair<Int, Int> {
        if (removedFrom.isEmpty()) {
            return Pair(oldStart, oldEnd)
        }

        val sortedIndices = removedFrom.sorted()
        val actualStart = if (oldStart > oldEnd) oldEnd else oldStart
        val actualEnd = if (oldStart > oldEnd) oldStart else oldEnd
        val removedAll = oldStart == oldEnd

        val newStart: Int
        val newEnd: Int
        if (removedAll) {
            // return the beginning of removed selection and end of removed selection
            newStart = sortedIndices.first()
            newEnd = sortedIndices.last() - ((sortedIndices.size - 1) * marker.length)
        } else {
            // return the normal modified selection
            val isFirstRemoved: Boolean
            newStart = if (sortedIndices.first() < actualStart) {
                val diff = actualStart - sortedIndices.first()
                if (diff <= marker.length) {
                    isFirstRemoved = true
                    actualStart - diff
                } else {
                    val isFirstTouchingThroughOtherMarkers
                            = rangeContainsOnlyMarkers(
                        oldText,
                        sortedIndices.first() + marker.length,
                        actualStart,
                        relevantMarkersMap[marker] ?: listOf()
                    )
                    if (isFirstTouchingThroughOtherMarkers) {
                        isFirstRemoved = true
                        actualStart - marker.length
                    } else {
                        isFirstRemoved = false
                        // need to account for adding the extra marker before start
                        actualStart + marker.length
                    }
                }
            } else {
                isFirstRemoved = true
                actualStart
            }

            newEnd = if (sortedIndices.last() >= actualEnd) {
                if (isFirstRemoved) {
                    actualEnd - (sortedIndices.size - 1) * marker.length
                } else {
                    // also need to account for adding the extra marker after end
                    actualEnd - (sortedIndices.size - 2) * marker.length + marker.length
                }
            } else {
                val diff = actualEnd - sortedIndices.last()
                if (diff < marker.length) {
                    // marker is overlapping with end
                    if (isFirstRemoved) {
                        // subtract 1 here because we are already subtracting the diff
                        actualEnd - (sortedIndices.size - 1) * marker.length - diff
                    } else {
                        // also need to account for adding the extra marker before start
                        actualEnd - (sortedIndices.size - 1) * marker.length - diff + marker.length
                    }
                } else {
                    // marker is not overlapping with end
                    if (isFirstRemoved) {
                        actualEnd - sortedIndices.size * marker.length
                    } else {
                        // also need to account for adding the extra marker before start
                        actualEnd - (sortedIndices.size - 1) * marker.length + marker.length
                    }
                }
            }
        }
        return Pair(newStart, newEnd)
    }

    /**
     * @property lineStarts the starts of each line in the selection
     * @property listMarker the marker for the given type of list
     * @property linesWithLists parallel to lineStarts, contains lines with
     *                          any kind of list
     * @property lineListMarkers parallel to lineStarts, contains the list
     *                           marker used for each line, or empty string
     *                           if there is no list
     * @property linesWithGivenListType parallel to lineStarts, contains lines
     *                                  with only the given type of list
     */
    private data class ListInfo(
        val lineStarts: List<Int>,
        val listMarker: String,
        val linesWithLists: List<Boolean>,
        val lineListMarkers: List<String>,
    ) {
        val linesWithGivenListType: List<Boolean> = lineListMarkers.map { curMarker ->
            curMarker == this.listMarker
        }
    }

    /**
     * Gets the starts of the lines that this selection encompasses.
     */
    private fun getLineStarts(text: String, rangeStart: Int, rangeEnd: Int): List<Int> {
        val actualStart = if (rangeStart > rangeEnd) rangeEnd else rangeStart
        val actualEnd = if (rangeStart > rangeEnd) rangeStart else rangeEnd
        var nextPos = 0
        var curPos: Int
        var passedEnd = false
        // get all line positions, filtering out null values for lines not in
        // selection
        return text.split("\n").mapNotNull { line ->
            curPos = nextPos
            nextPos += line.length + 1 // include newline
            // add start position, or null if the selection is not on this line
            val lineStart: Int? = if (actualStart < nextPos && !passedEnd) curPos else null
            if (actualEnd < nextPos) {
                passedEnd = true
            }
            lineStart
        }
    }

    /**
     * Gets information on what lines have lists on them and where. Contains
     * information on all list items as well as lists starting with the given marker.
     */
    private fun getLinesWithList(text: String, marker: String, rangeStart: Int, rangeEnd: Int): ListInfo {
        val selectionLineStarts: List<Int> = getLineStarts(text, rangeStart, rangeEnd)
        val lineListMarkers: List<String> = selectionLineStarts.map { lineStart ->
            // check if next non-whitespace character is a list marker
            val firstNonWhitespace = getFirstNonWhitespace(text, lineStart)
            // return value
            return@map if (firstNonWhitespace != null) {
                LIST_MARKERS.firstOrNull { curMarker ->
                    val trimmed = curMarker.trimStart()
                    trimmed == text.substring(firstNonWhitespace, trimmed.length)
                } ?: ""
            } else {
                ""
            }
        }
        return ListInfo(
            lineStarts = selectionLineStarts,
            listMarker = marker,
            linesWithLists = lineListMarkers.map { curMarker -> curMarker != "" },
            lineListMarkers = lineListMarkers,
        )
    }

    /**
     * Returns the first index in the given line that is not whitespace, or
     * null if the entire line is whitespace.
     */
    private fun getFirstNonWhitespace(text: String, lineStart: Int): Int? {
        // check if next non-whitespace character is a list marker
        var next: Int = lineStart
        while (text[next].isWhitespace() && text[next] != '\n') {
            next++
        }
        return if (text[next] == '\n') null else next
    }

    private fun convertOtherListMarkersToGivenMarker(text: String, listInfo: ListInfo, start: Int, end: Int): MarkdownResult {
        val actualStart = if (start > end) end else start
        val actualEnd = if (start > end) start else end
        var pos = 0
        var newText = ""
        var startSelectionDiff = 0
        var endSelectionDiff = 0
        listInfo.lineStarts.forEachIndexed { i, lineStart ->
            if (listInfo.linesWithLists[i] && !listInfo.linesWithGivenListType[i]) {
                // convert to given list type
                val firstNonWhitespace: Int = requireNotNull(getFirstNonWhitespace(text, lineStart),
                    { "Something is very wrong with MD list handling" })
                val trimmedOld = listInfo.lineListMarkers[i].trimStart()
                val trimmedNew = listInfo.listMarker.trimStart()
                newText += text.substring(pos, firstNonWhitespace)
                newText += trimmedNew
                pos = firstNonWhitespace + trimmedOld.length
                if (actualStart >= pos) {
                    startSelectionDiff += trimmedNew.length - trimmedOld.length
                }
                if (actualEnd >= pos) {
                    endSelectionDiff += trimmedNew.length - trimmedOld.length
                }
            }
        }
        // get remaining bit of text
        newText += text.substring(pos)
        return MarkdownResult(
            newText = newText,
            newSelectionStart = actualStart + startSelectionDiff,
            newSelectionEnd = actualEnd + endSelectionDiff,
        )
    }

    /**
     * Un-mark all list markers in the given selection, removing indentations.
     */
    private fun unMarkListMarkers(text: String, listInfo: ListInfo, start: Int, end: Int): MarkdownResult {
        val actualStart = if (start > end) end else start
        val actualEnd = if (start > end) start else end
        var pos = 0
        var newText = ""
        var startSelectionDiff = 0
        var endSelectionDiff = 0
        listInfo.lineStarts.forEachIndexed { i, lineStart ->
            if (listInfo.linesWithLists[i]) {
                // convert to given list type
                val firstNonWhitespace: Int = requireNotNull(getFirstNonWhitespace(text, lineStart),
                    { "Something is very wrong with MD list handling" })
                newText += text.substring(pos, lineStart)
                pos = firstNonWhitespace + listInfo.lineListMarkers[i].trimStart().length
                if (actualStart >= lineStart) {
                    startSelectionDiff += if (actualStart < pos) {
                        // move to beginning of line
                        lineStart - actualStart
                    } else {
                        // move left by deleted amount (including indents)
                        lineStart - pos
                    }
                }
                if (actualEnd >= lineStart) {
                    endSelectionDiff += if (actualEnd < pos) {
                        // move to beginning of line
                        lineStart - actualEnd
                    } else {
                        // move left by deleted amount (including indents)
                        lineStart - pos
                    }
                }
            }
        }
        // get remaining bit of text
        newText += text.substring(pos)
        return MarkdownResult(
            newText = newText,
            newSelectionStart = actualStart + startSelectionDiff,
            newSelectionEnd = actualEnd + endSelectionDiff,
        )
    }

    /**
     * Mark all lines in the selection with list markers.
     */
    private fun markListMarkers(text: String, listInfo: ListInfo, start: Int, end: Int): MarkdownResult {
        val actualStart = if (start > end) end else start
        val actualEnd = if (start > end) start else end
        var pos = 0
        var newText = ""
        var startSelectionDiff = 0
        var endSelectionDiff = 0
        listInfo.lineStarts.forEach { lineStart ->
            newText += text.substring(pos, lineStart)
            newText += listInfo.listMarker
            pos = lineStart
            if (actualStart >= lineStart) {
                startSelectionDiff += listInfo.listMarker.length
            }
            if (actualEnd >= lineStart) {
                endSelectionDiff += listInfo.listMarker.length
            }
        }
        // get remaining bit of text
        newText += text.substring(pos)
        return MarkdownResult(
            newText = newText,
            newSelectionStart = actualStart + startSelectionDiff,
            newSelectionEnd = actualEnd + endSelectionDiff,
        )
    }
}