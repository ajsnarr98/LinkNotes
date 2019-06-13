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
        fun newEmpty(): Note {
            return Note("54321")
        }
    }

    fun addEntry(entry: Entry) {
        entries.add(entry)
    }
}