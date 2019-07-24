package com.ajsnarr.peoplenotes.db

import java.util.*

class Entry(val id: String? = null,
            val type: EntryType? = null,
            val content: EntryContent? = null,
            var dateCreated: Date? = null,
            val datesEdited: MutableList<Date>? = null,
            val subEntries: MutableList<Entry>? = null
)