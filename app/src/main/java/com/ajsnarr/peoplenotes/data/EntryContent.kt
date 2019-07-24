package com.ajsnarr.peoplenotes.data

data class EntryContent(var content: String) {
    companion object {
        val EMPTY = EntryContent("")

        fun fromDBObj(other: com.ajsnarr.peoplenotes.db.EntryContent): EntryContent {
            return EntryContent(content=other.content!!)
        }
    }
}