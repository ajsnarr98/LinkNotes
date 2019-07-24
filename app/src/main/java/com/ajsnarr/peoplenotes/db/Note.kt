package com.ajsnarr.peoplenotes.db

data class Note(
    var id: String? = null, // if ID is null or empty string, creates a new id when it is inserted
    var type: String? = null,
    var name: String? = null,
    val nicknames: MutableList<String>? = null,
    var mainPicture: Picture? = null,
    val pictures: MutableList<Picture>? = null,
    val tags: MutableList<Tag>? = null,
    val entries: MutableList<Entry>? = null,
    val notes: MutableList<Note>? = null
)