package com.ajsnarr.peoplenotes.data.db

import java.util.*


data class Note(
    val id: String? = null, // if ID is null or blank string (at most whitespace chars), creates a new id when it is inserted
    val type: String? = null,
    val name: String? = null,
    val dateCreated: Date? = null,
    val lastDateEdited: Date? = null,
    val nicknames: MutableList<String>? = null,
    val mainPicture: Picture? = null,
    val pictures: MutableList<Picture>? = null,
    val tags: MutableList<Tag>? = null,
    val entries: MutableList<Entry>? = null,
    val notes: MutableList<Note>? = null
) : DBObject<com.ajsnarr.peoplenotes.data.Note> {

    companion object {
        fun fromAppObject(other: com.ajsnarr.peoplenotes.data.Note) : Note {
            return Note(
                id = other.id,
                type = other.type,
                name = other.name,
                dateCreated = other.dateCreated,
                lastDateEdited = other.lastDateEdited,
                nicknames = other.nicknames,
                mainPicture = if (other.mainPicture != null) Picture.fromAppObject(
                    other.mainPicture!!
                ) else null,
                pictures = other.pictures.map { url ->
                    Picture.fromAppObject(
                        url
                    )
                }.toMutableList(),
                tags = other.tags.map { tag -> Tag.fromAppObject(tag) }.toMutableList(),
                entries = other.entries.map { entry ->
                    Entry.fromAppObject(
                        entry
                    )
                }.toMutableList(),
                notes = other.notes?.map { note -> fromAppObject(note) }?.toMutableList()
            )
        }
    }

    /**
     * A note is marked as a new note, when it has no valid ID.
     *
     * An invalid ID will either be null or blank (at most whitespace chars).
     */
    fun hasID(): Boolean {
        return this.id == null || this.id.isBlank()
    }

    /**
     * Returns a new note with the given ID.
     */
    fun withId(id: String): Note {
        return this.copy(id=id)
    }

    override fun toAppObject(): com.ajsnarr.peoplenotes.data.Note {
        return com.ajsnarr.peoplenotes.data.Note(
            id = this.id!!,
            _type = this.type!!,
            _name = this.name!!,
            dateCreated = this.dateCreated!!,
            lastDateEdited = this.lastDateEdited!!,
            nicknames = this.nicknames!!,
            mainPicture = this.mainPicture?.url,
            pictures = this.pictures!!.map { it.url!! }.toMutableList(),
            tags = this.tags!!.map { it.toAppObject() }.toMutableList(),
            entries = toAppEntryList(),
            notes = this.notes?.map { it.toAppObject() }?.toMutableList()
        )
    }

    private fun toAppEntryList(): com.ajsnarr.peoplenotes.data.EntryList {
        return com.ajsnarr.peoplenotes.data.EntryList.fromCollection(
            entries?.map { it.toAppObject() } ?: listOf()
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
        return id?.hashCode() ?: 0
    }
}
