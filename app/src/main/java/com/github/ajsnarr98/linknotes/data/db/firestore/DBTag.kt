package com.github.ajsnarr98.linknotes.data.db.firestore

import com.github.ajsnarr98.linknotes.data.db.DBObject

data class DBTag(
    val text: String? = null,
    val color: DBColor? = null
) : DBObject<com.github.ajsnarr98.linknotes.data.Tag> {
    companion object {
        fun fromAppObject(other: com.github.ajsnarr98.linknotes.data.Tag) : DBTag {
            return DBTag(
                text = other.text,
                color = DBColor.fromAppObject(other.color)
            )
        }
    }

    override fun toAppObject(): com.github.ajsnarr98.linknotes.data.Tag {
        return com.github.ajsnarr98.linknotes.data.Tag(
            text = text!!,
            color = color!!.toAppObject()
        )
    }
}
