package com.github.ajsnarr98.linknotes.network.storage.firestore

import com.github.ajsnarr98.linknotes.network.domain.EntryType
import com.github.ajsnarr98.linknotes.network.storage.DBObject

data class DBEntryType(val type: String? = null):
    DBObject<EntryType<*>> {

    companion object {
        fun fromAppObject(other: EntryType<*>) : DBEntryType {
            return DBEntryType(type = other.value)
        }
    }

    override fun toAppObject(): EntryType<*> {
        return EntryType.forValue(type!!)
    }
}
