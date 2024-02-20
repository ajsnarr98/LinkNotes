package com.github.ajsnarr98.linknotes.desktop.util

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.res.ClassLoaderResourceLoader
import androidx.compose.ui.res.ResourceLoader
import java.io.BufferedReader
import java.io.FileNotFoundException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.IllegalArgumentException
import java.util.stream.Collectors

class RealResourceFileLoader() : ResourceFileLoader {
    @OptIn(ExperimentalComposeUiApi::class)
    private val loader: ResourceLoader = ClassLoaderResourceLoader()

    @OptIn(ExperimentalComposeUiApi::class)
    override fun load(resourcePath: String): InputStream {
        return try {
            loader.load(resourcePath)
        } catch (e: IllegalArgumentException) {
            throw FileNotFoundException(e.message)
        }
    }
}

interface ResourceFileLoader {
    /**
     * Load resource at the given path as a string.
     *
     * It is recommended to call this on an IO Dispatcher.
     */
    @Throws(FileNotFoundException::class)
    fun loadAsString(resourcePath: String): String {
        return BufferedReader(InputStreamReader(this.load(resourcePath))).use {
            it.lines().collect(Collectors.joining("\n")).trim()
        }
    }

    /**
     * Load resource at the given path.
     *
     * It is recommended to call this on an IO Dispatcher.
     */
    @Throws(FileNotFoundException::class)
    fun load(resourcePath: String): InputStream
}