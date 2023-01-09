package com.github.ajsnarr98.linknotes.network.storage.firestore

import com.github.ajsnarr98.linknotes.network.domain.EntryPriority
import com.github.ajsnarr98.linknotes.network.storage.DBObject

class DBEntryPriority(
    val priority: String? = null
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