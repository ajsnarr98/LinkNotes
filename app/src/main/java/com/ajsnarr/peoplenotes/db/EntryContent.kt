package com.ajsnarr.peoplenotes.db

data class EntryContent(var content: String? = null)
    : DBObject<com.ajsnarr.peoplenotes.data.EntryContent> {

    companion object {
        fun fromAppObject(other: com.ajsnarr.peoplenotes.data.EntryContent) : EntryContent {
            return EntryContent(content=other.content)
        }
    }

    override fun toAppObject(): com.ajsnarr.peoplenotes.data.EntryContent {
        return com.ajsnarr.peoplenotes.data.EntryContent(content=this.content!!)
    }
}
