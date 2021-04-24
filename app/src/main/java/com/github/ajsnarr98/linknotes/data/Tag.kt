package com.github.ajsnarr98.linknotes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Tag(val text: String, val color: Color = Color.randomTagColor())
    : AppDataObject, Parcelable, Serializable {

    /** Deep copy */
    fun copy(): Tag {
        return Tag(
            text = this.text,
            color = this.color.copy(),
        )
    }


    override fun equals(other: Any?): Boolean {
        // ignore color when checking for equality
        return this === other || other is Tag
                && this.text == other.text
    }

    override fun hashCode(): Int {
        return text.hashCode()
    }
}
