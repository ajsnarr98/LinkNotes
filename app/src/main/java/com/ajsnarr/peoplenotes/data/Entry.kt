package com.ajsnarr.peoplenotes.data

import java.lang.IllegalStateException
import java.util.*
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

class Entry(val id: UUID,
            val type: EntryType = EntryType.EMPTY,
            content: EntryContent = EntryContent.EMPTY,
            var dateCreated: Date = Date(),
            val datesEdited: MutableList<Date> = mutableListOf(),
            val subEntries: MutableList<Entry> = mutableListOf()
) {

    /**
     * Setup content add to datesEdited every time it is updated
     */
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
        fun fromDB(): Entry {
            return Entry("12345")
        }

        /**
         * Returns a new entry with a unique UUID.
         */
        fun newEmpty(): Entry {
            return Entry("12345")
        }
    }
}