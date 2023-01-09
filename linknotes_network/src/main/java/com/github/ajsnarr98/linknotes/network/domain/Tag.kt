package com.github.ajsnarr98.linknotes.network.domain

import com.github.ajsnarr98.linknotes.network.annotation.ColorInt
import com.github.ajsnarr98.linknotes.network.util.Color
import java.io.Serializable

data class Tag(
    val text: String,
    @ColorInt val color: Int = Color.randomTagColor(),
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
