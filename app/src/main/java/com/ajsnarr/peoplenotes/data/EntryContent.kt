package com.ajsnarr.peoplenotes.data

data class EntryContent(var content: String): DataObject<com.ajsnarr.peoplenotes.db.EntryContent> {
    companion object {
        val EMPTY = EntryContent("")

        fun fromDBObj(other: com.ajsnarr.peoplenotes.db.EntryContent): EntryContent {
            return EntryContent(content=other.content!!)
        }
    }

    override fun toDBObject(): com.ajsnarr.peoplenotes.db.EntryContent {
        return com.ajsnarr.peoplenotes.db.EntryContent(content=this.content)
    }
}