package com.github.ajsnarr98.linknotes.network.storage.firestore

import com.github.ajsnarr98.linknotes.network.domain.EntryContent
import com.github.ajsnarr98.linknotes.network.storage.DBObject

data class DBEntryContent(
    val content: String? = null,
    val type: String? = null,
) : DBObject<EntryContent> {

    companion object {
        fun fromAppObject(other: EntryContent) : DBEntryContent {
            return DBEntryContent(
                content = other.value,
                type = when (other) {
                    is EntryContent.Text -> EntryContent.Text.TYPE_NAME
                    is EntryContent.Images -> EntryContent.Images.TYPE_NAME
                }
            )
        }
    }

    override fun toAppObject(): EntryContent {
        return EntryContent.forTypeName(type!!, content!!)
    }
}
