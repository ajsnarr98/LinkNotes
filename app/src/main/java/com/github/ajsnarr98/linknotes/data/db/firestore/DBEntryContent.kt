package com.github.ajsnarr98.linknotes.data.db.firestore

import com.github.ajsnarr98.linknotes.data.EntryContent
import com.github.ajsnarr98.linknotes.data.db.DBObject

data class DBEntryContent(var content: String? = null)
    : DBObject<EntryContent> {

    companion object {
        fun fromAppObject(other: EntryContent) : DBEntryContent {
            return DBEntryContent(content = other.value)
        }
    }

    override fun toAppObject(): EntryContent {
        return EntryContent(value=this.content!!)
    }
}
