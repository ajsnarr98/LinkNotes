package com.github.ajsnarr98.linknotes.data.db.firestore

import com.github.ajsnarr98.linknotes.data.EntryType
import com.github.ajsnarr98.linknotes.data.db.DBObject

data class DBEntryType(var type: String? = null):
    DBObject<EntryType> {

    companion object {
        fun fromAppObject(other: EntryType) : DBEntryType {
            return DBEntryType(type = other.value)
        }
    }

    override fun toAppObject(): EntryType {
        return EntryType.forValue(type!!)
    }
}
