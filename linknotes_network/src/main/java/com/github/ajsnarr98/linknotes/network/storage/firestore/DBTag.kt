package com.github.ajsnarr98.linknotes.network.storage.firestore

import com.github.ajsnarr98.linknotes.network.storage.DBCollectionObject
import com.github.ajsnarr98.linknotes.network.domain.Tag

data class DBTag(
    val text: String? = null,
    val color: Int? = null,
) : DBCollectionObject<Tag> {

    override val id: String = text!!

    companion object {
        fun fromAppObject(other: Tag) : DBTag {
            return DBTag(
                text = other.text,
                color = other.color,
            )
        }
    }

    override fun toAppObject(): Tag {
        return Tag(
            text = text!!,
            color = color!!,
        )
    }

    override fun withID(id: String): DBCollectionObject<Tag> {
        throw UnsupportedOperationException("Tag cannot be changed here")
    }
}
