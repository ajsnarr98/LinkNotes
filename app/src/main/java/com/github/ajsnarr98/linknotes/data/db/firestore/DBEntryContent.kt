package com.github.ajsnarr98.linknotes.data.db.firestore

import com.github.ajsnarr98.linknotes.data.db.DBObject

data class DBEntryContent(var content: String? = null)
    : DBObject<com.github.ajsnarr98.linknotes.data.EntryContent> {

    companion object {
        fun fromAppObject(other: com.github.ajsnarr98.linknotes.data.EntryContent) : DBEntryContent {
            return DBEntryContent(content = other.value)
        }
    }

    override fun toAppObject(): com.github.ajsnarr98.linknotes.data.EntryContent {
        return com.github.ajsnarr98.linknotes.data.EntryContent(value=this.content!!)
    }
}
