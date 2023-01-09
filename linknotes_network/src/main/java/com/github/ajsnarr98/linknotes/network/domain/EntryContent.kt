package com.github.ajsnarr98.linknotes.network.domain

import java.io.Serializable

sealed interface EntryContent : AppDataObject, Serializable {

    val value: String

    companion object {
        fun forTypeName(type: String, value: String): EntryContent {
            return when (type) {
                Text.TYPE_NAME -> Text(value)
                Images.TYPE_NAME -> Images(value)
                else -> throw IllegalArgumentException("Unknown entry content type '$type'")
            }
        }
    }

    data class Text(override val value: String) : EntryContent {
        companion object {
            const val TYPE_NAME = "text"

            val EMPTY = Text("")
        }
    }

    /**
     * Image entries start with one line of metadata, then following
     * lines of images.
     */
    data class Images(override val value: String) : EntryContent {
        companion object {
            const val TYPE_NAME = "images"

            val EMPTY = Images("\n")

            private val imageRegex = Regex("!\\[[^\n]*]\\([^\n]+\\)")
            private val inParenthesis = Regex("\\(.*\\)")

            private fun specificImageRegex(imageUrl: String): Regex = Regex("!\\[[^\n]*]\\($imageUrl\\)")
        }

        /**
         * Returns a copy of the entry with an image added at the end.
         */
        fun withImageAppended(imageUrl: String): Images {
            return Images(this.value + "![]($imageUrl)\n")
        }

        /**
         * Returns a copy of the entry with the first image with matching
         * url removed.
         */
        fun withImageRemoved(imageUrl: String): Images {
            // replace first occurrence with empty string
            val newValue = specificImageRegex(imageUrl).replaceFirst(value, "")
            return if (value === newValue) this else Images(newValue)
        }

        val images: Iterable<String>
            get() = imageRegex
                .findAll(this.value, 0)
                .map { matchResult -> matchResult.value }
                .map { match -> inParenthesis.find(match)!!.value.trim('(', ')') }
                .toList()
    }
}
