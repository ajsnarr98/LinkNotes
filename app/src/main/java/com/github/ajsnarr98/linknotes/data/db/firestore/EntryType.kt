package com.github.ajsnarr98.linknotes.data.db.firestore

import com.github.ajsnarr98.linknotes.data.db.DBObject

data class EntryType(var type: String? = null):
    DBObject<com.github.ajsnarr98.linknotes.data.EntryType> {

    companion object {
        fun fromAppObject(other: com.github.ajsnarr98.linknotes.data.EntryType) : EntryType {
            return EntryType(type = other.value)
        }
    }

    override fun toAppObject(): com.github.ajsnarr98.linknotes.data.EntryType {
        return com.github.ajsnarr98.linknotes.data.EntryType(value=type!!)
    }
}
