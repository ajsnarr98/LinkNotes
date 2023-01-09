package com.github.ajsnarr98.linknotes.network.storage.firestore

import com.github.ajsnarr98.linknotes.network.storage.DBCollectionObject
import com.github.ajsnarr98.linknotes.network.domain.Tag

data class DBTag(
    val text: String? = null,
    val color: DBColor? = null,
) : DBCollectionObject<Tag> {

    override val id: String = text!!

    companion object {
        fun fromAppObject(other: Tag) : DBTag {
            return DBTag(
                text = other.text,
                color = DBColor.fromAppObject(other.color)
            )
        }
    }

    override fun toAppObject(): Tag {
        return Tag(
            text = text!!,
            color = color!!.toAppObject()
        )
    }

    override fun withID(id: String): DBCollectionObject<Tag> {
        throw UnsupportedOperationException("Tag cannot be changed here")
    }
}
