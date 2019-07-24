package com.ajsnarr.peoplenotes.data

import java.util.*

class Entry(val id: UUID,
            val type: EntryType = EntryType.EMPTY,
            content: EntryContent = EntryContent.EMPTY,
            var dateCreated: Date = Date(),
            val datesEdited: MutableList<Date> = mutableListOf(),
            val subEntries: MutableList<Entry> = mutableListOf()
) {

    // Setup content add to datesEdited every time it is updated
    var content: EntryContent = content
        set(value) {
            this.datesEdited.add(Date())
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
                content = EntryContent.fromDBObj(other.content!!),
                dateCreated = other.dateCreated!!,
                datesEdited = other.datesEdited!!,
                subEntries = other.subEntries!!.map { Entry.fromDBObj(it) }.toMutableList()
            )
        }

        /**
         * Returns a new entry with a unique UUID.
         */
        fun newEmpty(): Entry {
            return Entry("12345")
        }
    }
}