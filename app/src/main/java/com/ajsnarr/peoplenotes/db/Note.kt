package com.ajsnarr.peoplenotes.db


data class Note(
    val id: String? = null, // if ID is null or blank string (at most whitespace chars), creates a new id when it is inserted
    var type: String? = null,
    var name: String? = null,
    val nicknames: MutableList<String>? = null,
    var mainPicture: Picture? = null,
    val pictures: MutableList<Picture>? = null,
    val tags: MutableList<Tag>? = null,
    val entries: MutableList<Entry>? = null,
    val notes: MutableList<Note>? = null
) {

    /**
     * A note is marked as a new note, when it has no valid ID.
     *
     * An invalid ID will either be null or blank (at most whitespace chars).
     */
    fun isNewNote(): Boolean {
        return this.id == null || this.id.isBlank()
    }

    /**
     * Returns a new note with the given ID.
     */
    fun withId(id: String): Note {
        return this.copy(id=id)
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