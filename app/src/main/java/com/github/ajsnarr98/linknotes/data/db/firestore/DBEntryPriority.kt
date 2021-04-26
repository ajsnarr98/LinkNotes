package com.github.ajsnarr98.linknotes.data.db.firestore

import com.github.ajsnarr98.linknotes.data.EntryPriority
import com.github.ajsnarr98.linknotes.data.db.DBObject

class DBEntryPriority(
    var priority: String? = null
): DBObject<EntryPriority> {
    companion object {
        // priority will be always converted to a number string, or "" if null.
        fun fromAppObject(other: EntryPriority) : DBEntryPriority {
            return DBEntryPriority(priority = if (other.value == null) "" else other.value.toString())
        }
    }

    override fun toAppObject(): EntryPriority {
        return EntryPriority(value = priority?.toIntOrNull())
    }
}