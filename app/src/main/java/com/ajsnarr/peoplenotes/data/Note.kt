package com.ajsnarr.peoplenotes.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Note(
    val id: UUID?, // a null id will be assigned when stored in db
    var type: String = "",
    var name: String = "",
    val nicknames: MutableList<String> = mutableListOf(),
    var mainPicture: UUID? = null,
    val pictures: MutableList<UUID> = mutableListOf(),
    val tags: MutableList<Tag> = mutableListOf(),
    val entries: MutableList<Entry> = mutableListOf(),
    val notes: MutableList<Note>? = null // for noteGroup types
) : AppDataObject, Parcelable {
    companion object {

        const val DEFAULT_NOTE_TYPE = "default"

        fun newEmpty(): Note {
            return Note(null)
        }
    }

    // next entry is either 0 or one more than the most recently added entry
    val nextEntryID: String get() = if (entries.isEmpty()) "0" else entries.last().id.toBigInteger().inc().toString()

    /**
     * Add a new entry to this note.
     */
    fun addNewEntry() {
        entries.add(Entry(this.nextEntryID))
    }

    /**
     * Updates the existing entry with the matching ID.
     *
     * @return true if succesfully updated or false if failure
     */
    fun updateExistingEntry(updated: Entry): Boolean {
        val index = entries.indexOfFirst { entry -> entry.id == updated.id }
        if (index >= 0) {
            entries[index] = updated
            return true
        }
        return false
    }

    /**
     * A note is marked as a new note, when it has no valid ID.
     *
     * An invalid ID will either be null or blank (at most whitespace chars).
     */
    fun isNewNote(): Boolean {
        return this.id == null || this.id.isBlank()
    }

    /**
     * A note is valid when it has a non-blank name.
     */
    fun isValidNote(): Boolean {
        return name.isNotBlank()
    }

    /**
     *  Two notes are equal if they have the same id.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Note

        if (id != other.id) return false

        return true
    }

    /**
     *  Two notes are equal if they have the same id.
     */
    override fun hashCode(): Int {
        return id.hashCode()
    }
}
