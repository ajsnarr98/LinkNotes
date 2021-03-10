package com.github.ajsnarr98.linknotes.util

import com.github.ajsnarr98.linknotes.util.markdown.Markdown
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.math.exp

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
        actual = Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actualSelection = Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold left select"
        given = "word"
        selection = Pair(0, 0)
        expected = "****word"
        expectedSelection = Pair(2,2)
        actual = Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actualSelection = Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold right select"
        given = "word"
        selection = Pair(4, 4)
        expected = "word****"
        expectedSelection = Pair(6,6)
        actual = Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actualSelection = Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold full select"
        given = "word"
        selection = Pair(0, 4)
        expected = "**word**"
        expectedSelection = Pair(2,6)
        actual = Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actualSelection = Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold partial inside select"
        given = "word word word"
        selection = Pair(5, 9)
        expected = "word **word** word"
        expectedSelection = Pair(7, 11)
        actual = Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actualSelection = Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold inside select"
        given = "word word"
        selection = Pair(5, 5)
        expected = "word ****word"
        expectedSelection = Pair(7, 7)
        actual = Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actualSelection = Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
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
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word single 2"
        given = "**word** word"
        selection = Pair(3, 3)
        expected = "word word"
        expectedSelection = Pair(0, 4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word single 3"
        given = "word **word**"
        selection = Pair(7, 7)
        expected = "word word"
        expectedSelection = Pair(5,9)
        expectedMarkersToRemove = listOf(5, 11)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word single 4"
        given = "word **word** word"
        selection = Pair(7, 7)
        expected = "word word word"
        expectedSelection = Pair(5,9)
        expectedMarkersToRemove = listOf(5, 11)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 1"
        given = "**word**"
        selection = Pair(0, 8)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 2"
        given = "**word**"
        selection = Pair(0, 6)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 3"
        given = "**word**"
        selection = Pair(0, 7)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 4"
        given = "**word**"
        selection = Pair(2, 8)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 5"
        given = "**word**"
        selection = Pair(1, 7)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 6"
        given = "**word**"
        selection = Pair(1, 6)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold word selection 7"
        given = "**word**"
        selection = Pair(1, 8)
        expected = "word"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 6)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold partial selection 1"
        given = "**word word**"
        selection = Pair(0, 6)
        expected = "word** word**"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 11)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold partial selection 2"
        given = "**word word**"
        selection = Pair(7, 13)
        expected = "**word **word"
        expectedSelection = Pair(9,13)
        expectedMarkersToRemove = listOf(0, 11)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold partial selection 3"
        given = "**word word**"
        selection = Pair(2, 6)
        expected = "word** word**"
        expectedSelection = Pair(0,4)
        expectedMarkersToRemove = listOf(0, 11)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold partial selection 4"
        given = "**word word**"
        selection = Pair(7, 11)
        expected = "**word **word"
        expectedSelection = Pair(9,13)
        expectedMarkersToRemove = listOf(0, 11)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold partial selection 5"
        given = "**word word word**"
        selection = Pair(7, 11)
        expected = "**word **word** word**"
        expectedSelection = Pair(9,13)
        expectedMarkersToRemove = listOf(0, 16)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold multi selection 1"
        given = "**word** word **word**"
        selection = Pair(0, 22)
        expected = "word word word"
        expectedSelection = Pair(0,14)
        expectedMarkersToRemove = listOf(0, 6, 14, 20)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold multi selection 2"
        given = "**word** word **word**"
        selection = Pair(2, 22)
        expected = "word word word"
        expectedSelection = Pair(0,14)
        expectedMarkersToRemove = listOf(0, 6, 14, 20)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold multi selection 3"
        given = "**word** word **word**"
        selection = Pair(2, 20)
        expected = "word word word"
        expectedSelection = Pair(0,14)
        expectedMarkersToRemove = listOf(0, 6, 14, 20)
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold inverted selection 1"
        given = "**word** word **word**"
        selection = Pair(9, 13)
        expected = "**word** word **word**"
        expectedSelection = Pair(9,13)
        expectedMarkersToRemove = listOf()
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "bold inverted selection 2"
        given = "**word** word **word**"
        selection = Pair(8, 14)
        expected = "**word** word **word**"
        expectedSelection = Pair(8,14)
        expectedMarkersToRemove = listOf()
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        actualSelection = Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        markersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 2"
        given = "word"
        selection = Pair(0, 4)
        expected = "**word**"
        expectedSelection = Pair(2, 6)
        markersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 3"
        given = "**word**"
        selection = Pair(3, 5)
        expected = "**w**or**d**"
        expectedSelection = Pair(5, 7)
        markersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 4"
        given = "word"
        selection = Pair(2, 2)
        expected = "wo****rd"
        expectedSelection = Pair(4, 4)
        markersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 5"
        given = "word"
        selection = Pair(2, 2)
        expected = "wo****rd"
        expectedSelection = Pair(4, 4)
        markersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 5"
        given = "wo****rd"
        selection = Pair(4, 4)
        expected = "word"
        expectedSelection = Pair(2, 2)
        markersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 6"
        given = "****"
        selection = Pair(2, 2)
        expected = ""
        expectedSelection = Pair(0, 0)
        markersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        testName = "toggle 7"
        given = "**word **"
        selection = Pair(2, 6)
        expected = "word** **"
        expectedSelection = Pair(0, 4)
        markersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        // this case's behavior is subject to change
        testName = "unbold odd 1"
        given = "**word** word **word**"
        selection = Pair(6, 16)
        expected = "**word word word**"
        expectedSelection = Pair(6, 12)
        markersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)

        // this case's behavior is subject to change
        testName = "unbold odd 2"
        given = "**word** word **word**"
        selection = Pair(9, 13)
        expected = "**word** **word** **word**"
        expectedSelection = Pair(11, 15)
        markersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (markersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
        }
        actualSelection = if (markersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, markersToRemove)
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
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        actual = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        actualSelection = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        actual = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        actualSelection = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        actual = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        actualSelection = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        actual = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        actualSelection = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.ITALICS_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.ITALICS_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        actualSelection = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualMarkersToRemove = Markdown.markersToRemove(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        actual = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getUnMarkedText(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        }
        actualSelection = if (actualMarkersToRemove.isEmpty()) {
            Markdown.getNewSelectionOnMark(Markdown.BOLD_MARKER, selection.first, selection.second)
        } else {
            Markdown.getNewSelectionOnUnMark(given, Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
}