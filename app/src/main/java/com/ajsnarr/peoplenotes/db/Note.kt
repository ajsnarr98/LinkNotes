package com.ajsnarr.peoplenotes.db

/**
 * A note is marked as a new note, when it has no valid ID.
 *
 * An invalid ID will either be null or blank (at most whitespace chars).
 */
fun Note.isNewNote(): Boolean {
    return this.id == null || this.id.isBlank()
}


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
     * Returns a new note with the given ID.
     */
    fun withId(id: String): Note {
        return this.copy(id=id)
    }
}