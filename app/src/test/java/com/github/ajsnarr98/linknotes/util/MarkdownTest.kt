package com.github.ajsnarr98.linknotes.util

import com.github.ajsnarr98.linknotes.util.markdown.Markdown
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
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
        actualSelection = Markdown.getNewSelectionOnUnMark(Markdown.BOLD_MARKER, selection.first, selection.second, actualMarkersToRemove)
        assertEquals("test toRemove $testName", expectedMarkersToRemove, actualMarkersToRemove)
        assertEquals("test $testName", expected, actual)
        assertEquals("test selection $testName", expectedSelection, actualSelection)
    }
}