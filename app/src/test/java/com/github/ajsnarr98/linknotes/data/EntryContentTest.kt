package com.github.ajsnarr98.linknotes.data

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class EntryContentTest {
    @Test
    fun imageRegex1Test() {
        val text = "faldflj\n" +
                "hello 123 [derp](derp.com)\n" +
                "![derp image](derp.png)\n" +
                "Hi this line also has an image ![image](image.png) would you look at that\n" +
                "![](derp.png)\n" +
                "close but not an image: !](derp.png)" +
                "![] whoops this is not an image here" +
                "(hello.png)"
        val expectedMatches = setOf<String>(
            "![derp image](derp.png)",
            "![image](image.png)",
            "![](derp.png)",
        )
        val actual: Set<String> = EntryContent.imageRegex.findAll(text, 0)
            .map { matchResult -> matchResult.value }.toSet()
        assertEquals(expectedMatches, actual)
    }

    @Test
    fun images1Test() {
        val text = "faldflj\n" +
                "hello 123 [derp](derp.com)\n" +
                "![derp image](derp.png)\n" +
                "Hi this line also has an image ![image](image.png) would you look at that\n" +
                "![](derp.png)\n" +
                "close but not an image: !](derp.png)" +
                "![] whoops this is not an image here" +
                "(hello.png)"
        val expected = listOf<String>(
            "derp.png",
            "image.png",
            "derp.png",
        )
        val actual: List<String> = EntryContent(text).images.toList()
        assertEquals(expected, actual)
    }

    @Test
    fun images2Test() {
        val text = ""
        val expected = listOf<String>()
        val actual: List<String> = EntryContent(text).images.toList()
        assertEquals(expected, actual)
    }

    @Test
    fun deleteImageTest1() {
        val toRemove = "image.png"
        val text = "faldflj\n" +
                "hello 123 [derp](derp.com)\n" +
                "![derp image](derp.png)\n" +
                "Hi this line also has an image ![image](image.png) would you look at that\n" +
                "![](derp.png)\n" +
                "close but not an image: !](derp.png)" +
                "![] whoops this is not an image here" +
                "(hello.png)"
        val expected = "faldflj\n" +
                "hello 123 [derp](derp.com)\n" +
                "![derp image](derp.png)\n" +
                "Hi this line also has an image would you look at that\n" +
                "![](derp.png)\n" +
                "close but not an image: !](derp.png)" +
                "![] whoops this is not an image here" +
                "(hello.png)"
        val actual: String = EntryContent(text).apply { removeImage(toRemove) }.value
        assertEquals(expected, actual)
    }

    @Test
    fun deleteImageTest2() {
        val toRemove = "derp.png"
        val text = "faldflj\n" +
                "hello 123 [derp](derp.com)\n" +
                "![derp image](derp.png)\n" +
                "Hi this line also has an image ![image](image.png) would you look at that\n" +
                "![](derp.png)\n" +
                "close but not an image: !](derp.png)" +
                "![] whoops this is not an image here" +
                "(hello.png)"
        val expected = "faldflj\n" +
                "hello 123 [derp](derp.com)\n" +
                "\n" +
                "Hi this line also has an image ![image](image.png) would you look at that\n" +
                "![](derp.png)\n" +
                "close but not an image: !](derp.png)" +
                "![] whoops this is not an image here" +
                "(hello.png)"
        val actual: String = EntryContent(text).apply { removeImage(toRemove) }.value
        assertEquals(expected, actual)
    }
}
