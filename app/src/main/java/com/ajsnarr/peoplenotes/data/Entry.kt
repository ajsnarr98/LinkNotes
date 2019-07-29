package com.ajsnarr.peoplenotes.data

import java.util.*

class Entry(val id: UUID,
            var type: EntryType = EntryType.EMPTY,
            content: EntryContent = EntryContent.EMPTY,
            var dateCreated: Date = Date(),
            var lastDateEdited: Date = Date(),
            val subEntries: MutableList<Entry> = mutableListOf()
): AppDataObject {

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
}