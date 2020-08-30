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
    val entries: EntryList = EntryList.getEmpty(),
    val notes: MutableList<Note>? = null // for noteGroup types
) : AppDataObject, Parcelable {

    companion object {

        private const val DEFAULT_TYPE = Type.DEFAULT

        fun newEmpty(): Note {
            return Note(null)
        }
    }

    /**
     * Default note types.
     */
    object Type {
        const val DEFAULT = "default"
    }

    /**
     * Add a new entry to this note.
     */
    fun addNewEntry() {
        entries.add(Entry(id = entries.nextEntryID))
    }

    /**
     * Deletes an entry matching the given entry's id.
     */
    fun deleteEntry(entry: Entry) {
        deleteEntry(entry.id)
    }

    /**
     * Deletes the entry with the given id.
     *
     * Can be restored with restoreRecentlyDeletedEntries().
     */
    fun deleteEntry(entryID: String) {
        entries.removeWithEntryID(entryID)
    }

    /**
     * Fills up any empty fields with default values. Used after saving an
     * incomplete new note.
     */
    fun fillDefaults() {
        if (type.isEmpty()) type = DEFAULT_TYPE
        for (entry in entries){
            entry.fillDefaults()
        }

        if (notes != null) {
            for (n in notes) {
                n.fillDefaults()
            }
        }
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
     * This method should be called when this note is saved to the db.
     */
    fun onSaveNote() {
        entries.clearDeletedEntries()
    }

    /**
     * Restores the entries recently deleted since the last save.
     */
    fun restoreRecentlyDeletedEntries() {
        entries.restoreRecentlyDeleted()
    }

    /**
     * Updates the existing entry with the matching ID.
     *
     * @return true if succesfully updated or false if failure
     */
    fun updateExistingEntry(updated: Entry): Boolean {
        return entries.updateExisting(updated)
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
