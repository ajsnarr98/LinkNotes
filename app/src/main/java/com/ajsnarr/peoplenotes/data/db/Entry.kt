package com.ajsnarr.peoplenotes.data.db

import java.util.*

class Entry(val id: String? = null,
            val type: EntryType? = null,
            val content: EntryContent? = null,
            var dateCreated: Date? = null,
            val lastDateEdited: Date? = null,
            val subEntries: MutableList<Entry>? = null
): DBObject<com.ajsnarr.peoplenotes.data.Entry> {

    companion object {
        /**
         * Returns an entry from the database.
         */
        fun fromAppObject(other: com.ajsnarr.peoplenotes.data.Entry): Entry {
            return Entry(
                id = other.id,
                type = EntryType.fromAppObject(other.type),
                content = EntryContent.fromAppObject(other.content),
                dateCreated = other.dateCreated,
                lastDateEdited = other.lastDateEdited,
                subEntries = other.subEntries.map { fromAppObject(it) }.toMutableList()
            )
        }
    }

    override fun toAppObject(): com.ajsnarr.peoplenotes.data.Entry {
        return com.ajsnarr.peoplenotes.data.Entry(
            id = this.id!!,
            type = this.type!!.toAppObject(),
            mContent = this.content!!.toAppObject(),
            dateCreated = this.dateCreated!!,
            lastDateEdited = this.lastDateEdited!!,
            subEntries = this.subEntries!!.map { it.toAppObject() }.toMutableList()
        )
    }
}
