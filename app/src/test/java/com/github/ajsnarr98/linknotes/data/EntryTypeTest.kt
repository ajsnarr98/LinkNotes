package com.github.ajsnarr98.linknotes.data

import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import kotlin.reflect.KClass

@RunWith(JUnit4::class)
class EntryTypeTest {

    @Test
    fun antiTest() {
        val expected: KClass<out EntryType> = EntryType.DEFAULT::class
        val actual: KClass<out EntryType> = EntryType.CUSTOM::class
        Assert.assertNotEquals(expected, actual)
    }

    @Test
    fun defaultTest() {
        val expected: KClass<out EntryType> = EntryType.DEFAULT::class
        val actual: KClass<out EntryType> = EntryType.forValue("")::class
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun imagesTest() {
        val expected: KClass<out EntryType> = EntryType.IMAGES::class
        val actual: KClass<out EntryType> = EntryType.forValue("images")::class
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun custom1Test() {
        val expected: KClass<out EntryType> = EntryType.CUSTOM::class
        val actual: KClass<out EntryType> = EntryType.forValue("abc")::class
        Assert.assertEquals(expected, actual)
    }

    @Test
    fun custom2Test() {
        val expected: KClass<out EntryType> = EntryType.CUSTOM::class
        val actual: KClass<out EntryType> = EntryType.forValue("abc123")::class
        Assert.assertEquals(expected, actual)
    }
}