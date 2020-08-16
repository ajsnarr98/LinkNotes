package com.ajsnarr.peoplenotes.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
class Entry(val id: UUID,
            var type: EntryType = EntryType.EMPTY,
            private var mContent: EntryContent = EntryContent.EMPTY,
            var dateCreated: Date = Date(),
            var lastDateEdited: Date = Date(),
            var isDeletable: Boolean = true,
            val subEntries: MutableList<Entry> = mutableListOf()
): AppDataObject, Parcelable {

    // Setup content add to datesEdited every time it is updated
    var content: EntryContent = mContent
        set(value) {
            this.lastDateEdited = Date() // TODO - update parent note or entry as well
            mContent = value
            field = value
        }
        get() = field

    /**
     * Add a new sub entry.
     */
    fun addSubEntry(subEntry: Entry) = subEntries.add(subEntry)
}
