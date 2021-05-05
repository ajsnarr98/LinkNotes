package com.github.ajsnarr98.linknotes.data

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
data class Entry(val id: UUID,
            var type: EntryType = EntryType.DEFAULT(),
            private var mContent: EntryContent = EntryContent.EMPTY,
            var priority: EntryPriority = EntryPriority.LOWEST,
            var dateCreated: Date = Date(),
            var lastDateEdited: Date = Date(),
            var isDeletable: Boolean = true,
            val subEntries: MutableList<Entry> = mutableListOf(),
): AppDataObject, Parcelable, Serializable {

    companion object {
        fun forType(
            id: UUID,
            type: EntryType,
            isDeletable: Boolean = true,
            priority: EntryPriority = EntryPriority.LOWEST,
        ): Entry {
            return when (type) {
                is EntryType.DEFAULT -> Entry(id = id, type = type, priority = priority, isDeletable = isDeletable)
                is EntryType.IMAGES  -> Entry(
                    id = id,
                    type = type,
                    mContent = EntryContent.IMAGES_START,
                    priority = priority,
                    isDeletable = isDeletable,
                )
                is EntryType.CUSTOM -> Entry(id = id, type = type, priority = priority, isDeletable = isDeletable)
            }
        }
    }

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

    /** Deep copy */
    fun copy(): Entry {
        return Entry(
            id = this.id,
            type = this.type,
            mContent = this.mContent.copy(),
            dateCreated = this.dateCreated,
            lastDateEdited = this.lastDateEdited,
            isDeletable = this.isDeletable,
            subEntries = this.subEntries.map { it.copy() }.toMutableList(),
        )
    }

    /**
     * Meant for entries of type EntryType.IMAGES.
     *
     * Adds an image to the end of the entry.
     */
    fun appendImage(imageUrl: String) {
        mContent.appendImage(imageUrl)
    }

    /**
     * Meant for entries of type EntryType.IMAGES.
     *
     * Removes the first instance of an image with a matching url.
     *
     * @return true if successful, false if not
     */
    fun removeImage(imageUrl: String): Boolean {
        return mContent.removeImage(imageUrl)
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
        if (type.value.isEmpty()) type = EntryType.DEFAULT()
        for (entry in subEntries) {
            entry.fillDefaults()
        }
    }
}
