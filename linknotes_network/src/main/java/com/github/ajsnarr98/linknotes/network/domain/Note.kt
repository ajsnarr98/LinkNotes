package com.github.ajsnarr98.linknotes.network.domain

import com.github.ajsnarr98.linknotes.data.UUID
import java.io.Serializable
import java.util.Date

class Note(
    val id: UUID?, // a null id will be assigned when stored in db
    val type: String,
    val name: String,
    val timeCreated: Date,
    val lastTimeEdited: Date,
    val nicknames: List<String>,
    val mainPicture: UUID?,
    val pictures: List<UUID>,
    val tags: Set<Tag>,
    val entries: EntryList,
    val notes: List<Note>?, // for noteGroup types
) : AppDataObject, Serializable {

    companion object {
        private const val DEFAULT_TYPE = Type.DEFAULT

        fun newEmpty(): Note {
            return Note(
                id = null, // start off without an id
                type = "",
                name = "",
                timeCreated = Date(),
                lastTimeEdited = Date(),
                nicknames = mutableListOf(),
                mainPicture = null,
                pictures = mutableListOf(),
                tags = mutableSetOf(),
                entries = EntryList.getEmpty().withNewEntry(
                    Entry.forType(
                        id = Entry.DEFAULT_INVALID_UUID,
                        EntryType.Images,
                        isDeletable = false,
                        priority = EntryPriority.PINNED
                    )
                ),
                notes = null,
            )
        }
    }

    /**
     * Default note types.
     */
    object Type {
        const val DEFAULT = "default"
    }

    /**
     * Similar to the copy() method, but also updates the last edited time automatically.
     */
    fun edited(
        id: UUID? = this.id,
        type: String = this.type,
        name: String = this.name,
        timeCreated: Date = this.timeCreated,
        nicknames: List<String> = this.nicknames,
        mainPicture: UUID? = this.mainPicture,
        pictures: List<UUID> = this.pictures,
        tags: Set<Tag> = this.tags,
        entries: EntryList = this.entries,
        notes: List<Note>? = this.notes,
    ): Note = Note(
        id = id,
        type =  type,
        name =  name,
        timeCreated =  timeCreated,
        nicknames =  nicknames,
        mainPicture =  mainPicture,
        pictures =  pictures,
        tags =  tags,
        entries =  entries,
        notes =  notes,
        lastTimeEdited = Date(),
    )

    fun withPicture(picture: UUID): Note = edited(
        pictures = this.pictures + picture
    )

    /**
     * Add the given entry to this note, ignoring its id and giving the
     * inserted entry an appropriate id. Then sorts entries by priority.
     */
    fun withNewEntry(entry: Entry<*>): Note = edited(
        entries = entries.withNewEntry(entry),
    )

    /**
     * Returns note with an entry matching the given entry's id deleted.
     */
    fun withDeletedEntry(entry: Entry<*>): Note = withDeletedEntry(entry.id)

    /**
     * Returns note with an entry matching the given id deleted.
     */
    fun withDeletedEntry(entryID: String): Note = edited(
        entries = entries.withEntryRemovedByID(entryID)
    )

    /**
     * Fills up any empty fields with default values. Used after saving an
     * incomplete new note.
     */
    fun withFilledDefaults(): Note = edited(
        type = this.type.ifEmpty { DEFAULT_TYPE },
        entries = this.entries.withFilledDefaults(),
        notes = this.notes?.map { n -> n.withFilledDefaults() },
    )

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
     * Returns a new note with the existing entry with the matching ID updated.
     *
     * Entries are then re-sorted as needed.
     *
     * Nothing happens if no matching entry is found.
     */
    fun withUpdatedEntry(updated: Entry<*>): Note {
        val newEntries = this.entries.withUpdated(updated)
        return if (newEntries === this.entries) {
            // do nothing
            this
        } else {
            edited(entries = newEntries)
        }
    }

    /**
     * Returns true if this note or any child notes contain the given tag.
     */
    fun containsTag(tag: Tag): Boolean {
        return this.tags.contains(tag) || notes != null && notes.any { it.containsTag(tag) }
    }

    /**
     * Returns true if this note not only has the same id as other, but also
     * has the same entries, tags, etc.
     */
    fun exactEquals(other: Any?): Boolean {
        return this === other || other is Note
                && this.id == other.id
                && this.type == other.type
                && this.name == other.name
                && this.timeCreated == other.timeCreated
                && this.lastTimeEdited == other.lastTimeEdited
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
