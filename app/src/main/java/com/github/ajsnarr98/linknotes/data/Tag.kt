package com.github.ajsnarr98.linknotes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Tag(val text: String, val color: Color = Color.randomTagColor())
    : AppDataObject, Parcelable {

    override fun equals(other: Any?): Boolean {
        // ignore color when checking for equality
        return this === other || other is Tag
                && this.text == other.text
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }
}
