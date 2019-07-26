package com.ajsnarr.peoplenotes.data

import java.util.*

class Entry(val id: UUID,
            var type: EntryType = EntryType.EMPTY,
            content: EntryContent = EntryContent.EMPTY,
            var dateCreated: Date = Date(),
            var lastDateEdited: Date = Date(),
            val subEntries: MutableList<Entry> = mutableListOf()
): DataObject<com.ajsnarr.peoplenotes.db.Entry> {

    // Setup content add to datesEdited every time it is updated
    var content: EntryContent = content
        set(value) {
            this.lastDateEdited = Date()
            field = value
        }
        get() = field

    /**
     * Add a new sub entry.
     */
    fun addSubEntry(subEntry: Entry) = subEntries.add(subEntry)

    companion object {
        /**
         * Returns an entry from the database.
         */
        fun fromDBObj(other: com.ajsnarr.peoplenotes.db.Entry): Entry {
            return Entry(
                id = other.id!!,
                type = EntryType.fromDBObj(other.type!!),
                content = EntryContent.fromDBObj(other.content!!),
                dateCreated = other.dateCreated!!,
                lastDateEdited = other.lastDateEdited!!,
                subEntries = other.subEntries!!.map { Entry.fromDBObj(it) }.toMutableList()
            )
        }
    }

    override fun toDBObject(): com.ajsnarr.peoplenotes.db.Entry {
        return com.ajsnarr.peoplenotes.db.Entry(
            id = this.id,
            type = this.type.toDBObject(),
            content = this.content.toDBObject(),
            dateCreated = this.dateCreated,
            lastDateEdited = this.lastDateEdited,
            subEntries = this.subEntries.map { it.toDBObject() }.toMutableList()
        )
    }
}