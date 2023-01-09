package com.github.ajsnarr98.linknotes.network.domain

import java.io.Serializable

data class Tag(
    val text: String,
    val color: Color = Color.randomTagColor(),
) : AppDataObject, Serializable {

    override fun equals(other: Any?): Boolean {
        // ignore color when checking for equality
        return this === other || other is Tag
                && this.text == other.text
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }
}
