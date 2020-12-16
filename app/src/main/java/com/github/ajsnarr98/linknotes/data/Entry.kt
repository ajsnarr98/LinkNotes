package com.github.ajsnarr98.linknotes.data

import android.os.Parcelable
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Entry(val id: UUID,
            var type: EntryType = EntryType.DEFAULT,
            private var mContent: EntryContent = EntryContent.EMPTY,
            var dateCreated: Date = Date(),
            var lastDateEdited: Date = Date(),
            var isDeletable: Boolean = true,
            val subEntries: MutableList<Entry> = mutableListOf()
): AppDataObject, Parcelable {

    /**
     * Note containing this list of entries.
     */
    @IgnoredOnParcel
    var parentNote: Note? = null
        set(value) {
            field = value
            this.subEntries.forEach { entry -> entry.parentNote = value }
        }

    // Setup content add to datesEdited every time it is updated
    @IgnoredOnParcel
    var content: EntryContent = mContent
        set(value) {
            this.lastDateEdited = Date()
            parentNote?.onEditNote()
            mContent = value
            field = value
        }

    /**
     * Add a new sub entry.
     */
    fun addSubEntry(subEntry: Entry) = subEntries.add(subEntry)

    /**
     * Fills up any empty fields with default values. Used after saving an
     * incomplete new note.
     */
    fun fillDefaults() {
        if (type.value.isEmpty()) type = EntryType.DEFAULT
        for (entry in subEntries) {
            entry.fillDefaults()
        }
    }
}
