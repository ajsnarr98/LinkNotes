package com.ajsnarr.peoplenotes.data

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
) : DataObject<com.ajsnarr.peoplenotes.db.Note> {
    companion object {

        const val DEFAULT_NOTE_TYPE = "default"

        fun fromDBNote(other: com.ajsnarr.peoplenotes.db.Note): Note {
            return Note(
                id = other.id!!,
                type = other.type!!,
                name = other.name!!,
                nicknames = other.nicknames!!,
                mainPicture = other.mainPicture?.url,
                pictures = other.pictures!!.map { it.url!! }.toMutableList(),
                tags = other.tags!!.map { Tag.fromDBTag(it) }.toMutableList(),
                entries = other.entries!!.map { Entry.fromDBObj(it) }.toMutableList(),
                notes = other.notes?.map { Note.fromDBNote(it) }?.toMutableList()
            )
        }

        fun newEmpty(): Note {
            return Note(null)
        }
    }

    val nextEntryID: String get() = entries.size.toString()

    /**
     * Add a new entry to this note.
     */
    fun addNewEntry() {
        entries.add(Entry(this.nextEntryID))
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

    override fun toDBObject(): com.ajsnarr.peoplenotes.db.Note {
        return com.ajsnarr.peoplenotes.db.Note(
            id = this.id,
            type = this.type,
            name = this.name,
            nicknames = this.nicknames,
            mainPicture = if (this.mainPicture != null) com.ajsnarr.peoplenotes.db.Picture(url=this.mainPicture) else null,
            pictures = this.pictures.map { url -> com.ajsnarr.peoplenotes.db.Picture(url=url) }.toMutableList(),
            tags = this.tags.map { it.toDBObject() }.toMutableList(),
            entries = this.entries.map { it.toDBObject() }.toMutableList(),
            notes = this.notes?.map { it.toDBObject() }?.toMutableList()
        )
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