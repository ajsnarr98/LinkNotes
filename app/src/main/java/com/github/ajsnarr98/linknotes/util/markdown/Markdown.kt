package com.github.ajsnarr98.linknotes.util.markdown

/**
 * A collection of helper functions for markdown editing.
 */
object Markdown {
    const val ITALICS_MARKER = "_"
    const val BOLD_MARKER = "**"
    const val STRIKE_THROUGH_MARKER = "~~"

    /**
     * Mapping of markers (value) that must be accounted for when checking
     * for a given marker (key).
     */
    val relevantMarkersMap = mapOf<String, List<String>>(
        ITALICS_MARKER to listOf(BOLD_MARKER, STRIKE_THROUGH_MARKER),
        BOLD_MARKER    to listOf(ITALICS_MARKER, STRIKE_THROUGH_MARKER),
        STRIKE_THROUGH_MARKER to listOf(BOLD_MARKER, ITALICS_MARKER),
    ).withDefault { listOf() }

    data class MarkdownResult(
        val newText: String,
        val newSelectionStart: Int,
        val newSelectionEnd: Int,
    ) {

    }

    fun toggleMarker(text: String, marker: String, start: Int, end: Int): MarkdownResult {
        val markersToRemove = markersToRemove(text, marker, start, end)
        val newText = if (markersToRemove.isEmpty()) {
            getMarkedText(text, marker, start, end)
        } else {
            getUnMarkedText(text, marker, start, end, markersToRemove)
        }
        var newSelection = if (markersToRemove.isEmpty()) {
            getNewSelectionOnMark(marker, start, end)
        } else {
            getNewSelectionOnUnMark(text, marker, start, end, markersToRemove)
        }
        return MarkdownResult(newText, newSelection.first, newSelection.second)
    }

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
}