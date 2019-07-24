package com.ajsnarr.peoplenotes.data


data class Note(
    val id: UUID,
    var type: String = "",
    var name: String = "",
    val nicknames: MutableList<String> = mutableListOf(),
    var mainPicture: UUID? = null,
    val pictures: MutableList<UUID> = mutableListOf(),
    val tags: MutableList<Tag> = mutableListOf(),
    val entries: MutableList<Entry> = mutableListOf(),
    val notes: MutableList<Note>? = null // for noteGroup types
) {
    companion object {
        fun fromDBNote(other: com.ajsnarr.peoplenotes.db.Note): Note {
            return Note(
                id = other.id!!,
                type = other.type!!,
                name = other.name!!,
                nicknames = other.nicknames!!,
                mainPicture = other.mainPicture!!.url,
                pictures = other.pictures!!.map { it.url!! }.toMutableList(),
                tags = other.tags!!.map { Tag.fromDBTag(it) }.toMutableList(),
                entries = other.entries!!.map { Entry.fromDBObj(it) }.toMutableList(),
                notes = other.notes!!.map { Note.fromDBNote(it) }.toMutableList()
            )
        }

        fun newEmpty(): Note {
            return Note("54321")
        }
    }

    fun addEntry(entry: Entry) {
        entries.add(entry)
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