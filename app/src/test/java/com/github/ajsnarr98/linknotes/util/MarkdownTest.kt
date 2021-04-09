package com.github.ajsnarr98.linknotes.util

import com.github.ajsnarr98.linknotes.util.markdown.Markdown
import org.junit.Assert.*
import org.junit.Test

class MarkdownTest {
    @Test
    fun basicMarkBoldTest() {
        var testName: String
        var given: String
        var expected: String
        var actual: String
        var selection: Pair<Int, Int>
        var expectedSelection: Pair<Int, Int>
        var actualSelection: Pair<Int, Int>

        testName = "bold empty"
        given = ""
        selection = Pair(0, 0)
        expected = "****"
        expectedSelection = Pair(2,2)
        actual = Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actualSelection = Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold left select"
        given = "word"
        selection = Pair(0, 0)
        expected = "****word"
        expectedSelection = Pair(2,2)
        actual = Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actualSelection = Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold right select"
        given = "word"
        selection = Pair(4, 4)
        expected = "word****"
        expectedSelection = Pair(6,6)
        actual = Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actualSelection = Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold full select"
        given = "word"
        selection = Pair(0, 4)
        expected = "**word**"
        expectedSelection = Pair(2,6)
        actual = Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actualSelection = Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold partial inside select"
        given = "word word word"
        selection = Pair(5, 9)
        expected = "word **word** word"
        expectedSelection = Pair(7, 11)
        actual = Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actualSelection = Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold inside select"
        given = "word word"
        selection = Pair(5, 5)
        expected = "word ****word"
        expectedSelection = Pair(7, 7)
        actual = Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actualSelection = Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)
    }

    @Test
    fun basicUnMarkBoldTest() {
        var testName: String
        var given: String
        var expected: String
        var actual: String
        var selection: Pair<Int, Int>
        var expectedSelection: Pair<Int, Int>
        var actualSelection: Pair<Int, Int>
        var expectedMarkersToRemove: List<Int>
        var actualMarkersToRemove: List<Int>

        testName = "bold word single 1"
        given = "**word**"
        selection = Pair(3, 3)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word single 2"
        given = "**word** word"
        selection = Pair(3, 3)
        expected = "word word"
        expectedSelection = Pair(0, 4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word single 3"
        given = "word **word**"
        selection = Pair(7, 7)
        expected = "word word"
        expectedSelection = Pair(5,9)
        expectedMarkersToRemove = listOf(5, 11)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word single 4"
        given = "word **word** word"
        selection = Pair(7, 7)
        expected = "word word word"
        expectedSelection = Pair(5,9)
        expectedMarkersToRemove = listOf(5, 11)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 1"
        given = "**word**"
        selection = Pair(0, 8)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 2"
        given = "**word**"
        selection = Pair(0, 6)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 3"
        given = "**word**"
        selection = Pair(0, 7)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 4"
        given = "**word**"
        selection = Pair(2, 8)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 5"
        given = "**word**"
        selection = Pair(1, 7)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 6"
        given = "**word**"
        selection = Pair(1, 6)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 7"
        given = "**word**"
        selection = Pair(1, 8)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold partial selection 1"
        given = "**word word**"
        selection = Pair(0, 6)
        expected = "word** word**"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 11)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold partial selection 2"
        given = "**word word**"
        selection = Pair(7, 13)
        expected = "**word **word"
        expectedSelection = Pair(9,13)
        expectedMarkersToRemove = listOf(0, 11)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold partial selection 3"
        given = "**word word**"
        selection = Pair(2, 6)
        expected = "word** word**"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 11)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold partial selection 4"
        given = "**word word**"
        selection = Pair(7, 11)
        expected = "**word **word"
        expectedSelection = Pair(9,13)
        expectedMarkersToRemove = listOf(0, 11)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold partial selection 5"
        given = "**word word word**"
        selection = Pair(7, 11)
        expected = "**word **word** word**"
        expectedSelection = Pair(9,13)
        expectedMarkersToRemove = listOf(0, 16)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold multi selection 1"
        given = "**word** word **word**"
        selection = Pair(0, 22)
        expected = "word word word"
        expectedSelection = Pair(0,14)
        expectedMarkersToRemove = listOf(0, 6, 14, 20)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold multi selection 2"
        given = "**word** word **word**"
        selection = Pair(2, 22)
        expected = "word word word"
        expectedSelection = Pair(0,14)
        expectedMarkersToRemove = listOf(0, 6, 14, 20)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold multi selection 3"
        given = "**word** word **word**"
        selection = Pair(2, 20)
        expected = "word word word"
        expectedSelection = Pair(0,14)
        expectedMarkersToRemove = listOf(0, 6, 14, 20)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold inverted selection 1"
        given = "**word** word **word**"
        selection = Pair(9, 13)
        expected = "**word** word **word**"
        expectedSelection = Pair(9,13)
        expectedMarkersToRemove = listOf()
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold inverted selection 2"
        given = "**word** word **word**"
        selection = Pair(8, 14)
        expected = "**word** word **word**"
        expectedSelection = Pair(8,14)
        expectedMarkersToRemove = listOf()
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)
    }

    /**
     * Test to see if the end result is expected.
     */
    @Test
    fun basicToggleBoldTest() {
        var testName: String
        var given: String
        var expected: String
        var actual: String
        var selection: Pair<Int, Int>
        var expectedSelection: Pair<Int, Int>
        var actualSelection: Pair<Int, Int>
        var markersToRemove: List<Int>

        testName = "toggle 1"
        given = "**word** **word** **word**"
        selection = Pair(0, 26)
        expected = "word word word"
        expectedSelection = Pair(0, 14)
        markersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 2"
        given = "word"
        selection = Pair(0, 4)
        expected = "**word**"
        expectedSelection = Pair(2, 6)
        markersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 3"
        given = "**word**"
        selection = Pair(3, 5)
        expected = "**w**or**d**"
        expectedSelection = Pair(5, 7)
        markersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 4"
        given = "word"
        selection = Pair(2, 2)
        expected = "wo****rd"
        expectedSelection = Pair(4, 4)
        markersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 5"
        given = "word"
        selection = Pair(2, 2)
        expected = "wo****rd"
        expectedSelection = Pair(4, 4)
        markersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 5"
        given = "wo****rd"
        selection = Pair(4, 4)
        expected = "word"
        expectedSelection = Pair(2, 2)
        markersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 6"
        given = "****"
        selection = Pair(2, 2)
        expected = ""
        expectedSelection = Pair(0, 0)
        markersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 7"
        given = "**word **"
        selection = Pair(2, 6)
        expected = "word** **"
        expectedSelection = Pair(0, 4)
        markersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        // this case's behavior is subject to change
        testName = "unbold odd 1"
        given = "**word** word **word**"
        selection = Pair(6, 16)
        expected = "**word word word**"
        expectedSelection = Pair(6, 12)
        markersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        // this case's behavior is subject to change
        testName = "unbold odd 2"
        given = "**word** word **word**"
        selection = Pair(9, 13)
        expected = "**word** **word** **word**"
        expectedSelection = Pair(11, 15)
        markersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)
    }

    @Test
    fun toggleItalicInBoldTest() {
        var testName: String
        var given: String
        var expected: String
        var actual: String
        var selection: Pair<Int, Int>
        var expectedSelection: Pair<Int, Int>
        var actualSelection: Pair<Int, Int>
        var expectedMarkersToRemove: List<Int>
        var actualMarkersToRemove: List<Int>

        testName = "italic in bold 1"
        given = "**word**"
        selection = Pair(2, 6)
        expected = "**_word_**"
        expectedSelection = Pair(3, 7)
        expectedMarkersToRemove = listOf()
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        actual = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        actualSelection = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "italic in bold 2"
        given = "**word word**"
        selection = Pair(2, 6)
        expected = "**_word_ word**"
        expectedSelection = Pair(3, 7)
        expectedMarkersToRemove = listOf()
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        actual = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        actualSelection = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "italic in bold 3"
        given = "**_word_ word**"
        selection = Pair(2, 8)
        expected = "**word word**"
        expectedSelection = Pair(2, 6)
        expectedMarkersToRemove = listOf(2, 7)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        actual = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        actualSelection = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "italic in bold 4"
        given = "**_word_ word**"
        selection = Pair(4, 4)
        expected = "**word word**"
        expectedSelection = Pair(2, 6)
        expectedMarkersToRemove = listOf(2, 7)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        actual = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        actualSelection = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "unbold 1"
        given = "**_word_**"
        selection = Pair(4, 4)
        expected = "_word_"
        expectedSelection = Pair(0, 6)
        expectedMarkersToRemove = listOf(0, 8)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        actualSelection = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "unbold 2"
        given = "**_word_**"
        selection = Pair(3, 7)
        expected = "_word_"
        expectedSelection = Pair(1, 5)
        expectedMarkersToRemove = listOf(0, 8)
        actualMarkersToRemove = Markdown.surroundingMarkersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getSurroundingMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getSurroundingUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        actualSelection = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnSurroundingMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnSurroundingUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test selection $testName", expectedSelection, actualSelection)
    }

    @Test
    fun testRangeContainsOnlyMarkers() {
        var testName: String
        var given: String
        var start: Int
        var end: Int
        var markers: List<String>
        var expected: Boolean
        var actual: Boolean

        testName = "1"
        given = "**_/word_/**"
        start = 2
        end = 4
        markers = listOf("_", "/")
        expected = true
        actual = Markdown.rangeContainsOnlyMarkers(given, start, end, markers)
        assertEquals(testName, expected, actual)

        testName = "2"
        given = "**_word_**"
        start = 2
        end = 3
        markers = listOf("_", "/")
        expected = true
        actual = Markdown.rangeContainsOnlyMarkers(given, start, end, markers)
        assertEquals(testName, expected, actual)

        testName = "3"
        given = "**word**"
        start = 2
        end = 2
        markers = listOf("_", "/")
        expected = false
        actual = Markdown.rangeContainsOnlyMarkers(given, start, end, markers)
        assertEquals(testName, expected, actual)

        testName = "4"
        given = "**_ word_/**"
        start = 2
        end = 4
        markers = listOf("_", "/")
        expected = false
        actual = Markdown.rangeContainsOnlyMarkers(given, start, end, markers)
        assertEquals(testName, expected, actual)

        testName = "5"
        given = ""
        start = 0
        end = 0
        markers = listOf("_", "/")
        expected = false
        actual = Markdown.rangeContainsOnlyMarkers(given, start, end, markers)
        assertEquals(testName, expected, actual)
    }

    @Test
    fun basicMarkBulletListTest() {
        var testName: String
        var given: String
        var selection: Pair<Int, Int>
        var marker: String
        var expected: String
        var expectedSelection: Pair<Int, Int>
        var expectedRes: Markdown.MarkdownResult
        var actual: Markdown.MarkdownResult

        testName = "one line 1"
        given = "hello\n123\nabc\nderp"
        selection = Pair(0, 0)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n123\nabc\nderp"
        expectedSelection = Pair(2, 2)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "one line 2"
        given = "hello\n123\nabc\nderp"
        selection = Pair(5, 5)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n123\nabc\nderp"
        expectedSelection = Pair(7, 7)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "one line 3"
        given = "hello\n123\nabc\nderp"
        selection = Pair(0, 5)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n123\nabc\nderp"
        expectedSelection = Pair(2, 7)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "one line 4"
        given = "hello\n123\nabc\nderp"
        selection = Pair(8, 8)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "hello\n- 123\nabc\nderp"
        expectedSelection = Pair(10, 10)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "one line 5"
        given = "hello\n123\nabc\nderp"
        selection = Pair(13, 13)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "hello\n123\n- abc\nderp"
        expectedSelection = Pair(15, 15)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "one line 6"
        given = "hello\n123\nabc\nderp"
        selection = Pair(18, 18)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "hello\n123\nabc\n- derp"
        expectedSelection = Pair(20, 20)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "multiline 1"
        given = "hello\n123\nabc\nderp"
        selection = Pair(5, 6)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n- 123\nabc\nderp"
        expectedSelection = Pair(7, 10)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "multiline 2"
        given = "hello\n123\nabc\nderp"
        selection = Pair(0, 18)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n- 123\n- abc\n- derp"
        expectedSelection = Pair(2, 26)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "multiline 3"
        given = "hello\n123\nabc\nderp"
        selection = Pair(8, 18)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "hello\n- 123\n- abc\n- derp"
        expectedSelection = Pair(10, 24)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )
    }

    @Test
    fun basicUnMarkBulletListTest() {
        var testName: String
        var given: String
        var selection: Pair<Int, Int>
        var marker: String
        var expected: String
        var expectedSelection: Pair<Int, Int>
        var expectedRes: Markdown.MarkdownResult
        var actual: Markdown.MarkdownResult

        testName = "one line 1"
        given = "- hello\n- 123\n- abc\n- derp"
        selection = Pair(2, 2)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "hello\n- 123\n- abc\n- derp"
        expectedSelection = Pair(0, 0)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "one line 2"
        given = "- hello\n- 123\n- abc\n- derp"
        selection = Pair(3, 3)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "hello\n- 123\n- abc\n- derp"
        expectedSelection = Pair(1, 1)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "one line 3"
        given = "- hello\n- 123\n- abc\n- derp"
        selection = Pair(0, 0)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "hello\n- 123\n- abc\n- derp"
        expectedSelection = Pair(0, 0)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "one line 4"
        given = "- hello\n- 123\n- abc\n- derp"
        selection = Pair(8, 8)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n123\n- abc\n- derp"
        expectedSelection = Pair(8, 8)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "one line 5"
        given = "- hello\n- 123\n- abc\n- derp"
        selection = Pair(26, 26)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n- 123\n- abc\nderp"
        expectedSelection = Pair(24, 24)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "multiline 1"
        given = "- hello\n- 123\n- abc\n- derp"
        selection = Pair(0, 26)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "hello\n123\nabc\nderp"
        expectedSelection = Pair(0, 18)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "multiline 2"
        given = "- hello\n- 123\n- abc\n- derp"
        selection = Pair(9, 26)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n123\nabc\nderp"
        expectedSelection = Pair(8, 20)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "multiline 3"
        given = "- hello\n- 123\nabc\n- derp"
        selection = Pair(9, 24)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n123\nabc\nderp"
        expectedSelection = Pair(8, 20)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "multiline 4"
        given = "- hello\n123\n- abc\n- derp"
        selection = Pair(9, 24)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n123\nabc\nderp"
        expectedSelection = Pair(9, 20)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "multiline 5"
        given = "- hello\n123\n- abc\n    - derp"
        selection = Pair(9, 28)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n123\nabc\nderp"
        expectedSelection = Pair(9, 20)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )
    }

    @Test
    fun replaceMixedBulletListTest() {
        var testName: String
        var given: String
        var selection: Pair<Int, Int>
        var marker: String
        var expected: String
        var expectedSelection: Pair<Int, Int>
        var expectedRes: Markdown.MarkdownResult
        var actual: Markdown.MarkdownResult

        testName = "one line 1"
        given = "1. hello\n1. 123\n- abc\n- derp"
        selection = Pair(0, 0)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n1. 123\n- abc\n- derp"
        expectedSelection = Pair(0, 0)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        // this test's selection behavior is subject to change
        testName = "one line 2"
        given = "1. hello\n1. 123\n- abc\n- derp"
        selection = Pair(2, 2)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n1. 123\n- abc\n- derp"
        expectedSelection = Pair(2, 2)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        // this test's selection behavior is subject to change
        testName = "one line 3"
        given = "1. hello\n1. 123\n- abc\n- derp"
        selection = Pair(1, 1)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n1. 123\n- abc\n- derp"
        expectedSelection = Pair(1, 1)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "one line 4"
        given = "1. hello\n1. 123\n- abc\n- derp"
        selection = Pair(13, 15)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "1. hello\n- 123\n- abc\n- derp"
        expectedSelection = Pair(12, 14)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "one line 5"
        given = "1. hello\n1. 123\n- abc\n- derp"
        selection = Pair(18, 20)
        marker = Markdown.NUMBERED_LIST_MARKER
        expected = "1. hello\n1. 123\n1. abc\n- derp"
        expectedSelection = Pair(19, 21)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "multiline 1"
        given = "1. hello\n1. 123\n- abc\n- derp"
        selection = Pair(0, 28)
        marker = Markdown.NUMBERED_LIST_MARKER
        expected = "1. hello\n1. 123\n1. abc\n1. derp"
        expectedSelection = Pair(0, 30)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "multiline 2"
        given = "1. hello\n1. 123\n- abc\n- derp"
        selection = Pair(0, 28)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n- 123\n- abc\n- derp"
        expectedSelection = Pair(0, 26)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "multiline 3"
        given = "hello\n1. 123\n- abc\n- derp"
        selection = Pair(0, 25)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "hello\n- 123\n- abc\n- derp"
        expectedSelection = Pair(0, 24)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "multiline 4"
        given = "1. hello\n    1. 123\n- abc\nderp"
        selection = Pair(0, 30)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "- hello\n    - 123\n- abc\nderp"
        expectedSelection = Pair(0, 28)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )

        testName = "multiline 5"
        given = "1. hello\n1. 123\n- abc\n- derp"
        selection = Pair(12, 28)
        marker = Markdown.BULLET_LIST_MARKER
        expected = "1. hello\n- 123\n- abc\n- derp"
        expectedSelection = Pair(11, 27)
        expectedRes = Markdown.MarkdownResult(
            newText = expected,
            newSelectionStart = expectedSelection.first,
            newSelectionEnd = expectedSelection.second,
        )
        actual = Markdown.toggleListMarker(given, marker, selection.first, selection.second)
        assertEquals("$testName - text", expectedRes.newText, actual.newText)
        assertEquals("$testName - selection",
            Pair(expectedRes.newSelectionStart, expectedRes.newSelectionEnd),
            Pair(actual.newSelectionStart, actual.newSelectionEnd)
        )
    }
}