package com.github.ajsnarr98.linknotes.data

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class EntryContent(var value: String): AppDataObject, Parcelable, Serializable {
    companion object {

        val EMPTY get() = EntryContent("")

        /**
         * Image entries start with one line of metadata, then following
         * lines of images.
         */
        val IMAGES_START get() = EntryContent("\n")

        val imageRegex = Regex("!\\[[^\n]*]\\([^\n]+\\)")
        private val inParenthesis = Regex("\\(.*\\)")

        fun specificImageRegex(imageUrl: String): Regex = Regex("!\\[[^\n]*]\\($imageUrl\\)")
    }

    /**
     * Meant for entries of type EntryType.IMAGES.
     *
     * Adds an image to the end of the entry.
     */
    fun appendImage(imageUrl: String) {
        value += "![]($imageUrl)\n"
    }

    /**
     * Meant for entries of type EntryType.IMAGES.
     *
     * Removes the first instance of an image with a matching url.
     *
     * @return true if successful, false if not
     */
    fun removeImage(imageUrl: String): Boolean {
        // replace first occurrence with empty string
        val oldValue = value
        value = specificImageRegex(imageUrl).replaceFirst(value, "")
        return this.value != oldValue
    }

    @IgnoredOnParcel
    val images: Iterable<String>
        get() = imageRegex
            .findAll(this.value, 0)
            .map { matchResult -> matchResult.value }
            .map { match -> inParenthesis.find(match)!!.value.trim('(', ')') }
            .toList()
}
