package com.github.ajsnarr98.linknotes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class Note(
    val id: UUID?, // a null id will be assigned when stored in db
    var _type: String = "",
    var _name: String = "",
    val dateCreated: Date = Date(),
    var lastDateEdited: Date = Date(),
    val nicknames: MutableList<String> = mutableListOf(),
    var mainPicture: UUID? = null,
    val pictures: MutableList<UUID> = mutableListOf(),
    val tags: MutableSet<Tag> = mutableSetOf(),
    val entries: EntryList = EntryList.getEmpty(),
    val notes: MutableList<Note>? = null // for noteGroup types
) : AppDataObject, Parcelable {

    init {
        entries.parentNote = this
    }

    var type
        get() = _type
        set(value) {
            _type = value
            onEditNote()
        }

    var name
        get() = _name
        set(value) {
            _name = value
            onEditNote()
        }

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

    fun addPicture(picture: UUID) {
        pictures.add(picture)
        onEditNote()
    }

    /**
     * Add a new entry to this note.
     */
    fun addNewEntry() {
        entries.add(Entry(id = entries.nextEntryID))
        onEditNote()
    }

    /**
     * Makes a deep copy of this note.
     */
    fun copy(): Note {
        return Note(
            id = this.id,
            _type = this._type,
            _name = this._name,
            dateCreated = this.dateCreated,
            lastDateEdited = this.lastDateEdited,
            nicknames = this.nicknames.map { it }.toMutableList(),
            mainPicture = this.mainPicture,
            pictures = this.pictures.map { it }.toMutableList(),
            tags = this.tags.map { it.copy() }.toMutableSet(),
            entries = EntryList.fromCollection(this.entries.map { it.copy() }),
            notes = this.notes?.map { it.copy() }?.toMutableList(),
        )
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
        onEditNote()
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
    fun onSaveNote() {}

    /**
     * WARNING - This method is only meant for internal use within Note and its
     * properties.
     *
     * Called when the note is updated.
     */
    fun onEditNote() {
        lastDateEdited = Date()
    }

    /**
     * Updates the existing entry with the matching ID.
     *
     * @return true if succesfully updated or false if failure
     */
    fun updateExistingEntry(updated: Entry): Boolean {
        return entries.updateExisting(updated).also { isUpdated -> if (isUpdated) onEditNote() }
    }

    /**
     * Returns true if this note not only has the same id as other, but also
     * has the same entries, tags, etc.
     */
    fun exactEquals(other: Any?): Boolean {
        return this === other || other is Note
                && this.id == other.id
                && this._type == other._type
                && this._name == other._name
                && this.dateCreated == other.dateCreated
                && this.lastDateEdited == other.lastDateEdited
                && this.nicknames == other.nicknames
                && this.mainPicture == other.mainPicture
                && this.pictures == other.pictures
                && this.tags == other.tags
                && this.entries == other.entries
                && this.notes == other.notes
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
