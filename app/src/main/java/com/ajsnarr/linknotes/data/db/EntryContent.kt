package com.ajsnarr.linknotes.data.db

data class EntryContent(var content: String? = null)
    : DBObject<com.ajsnarr.linknotes.data.EntryContent> {

    companion object {
        fun fromAppObject(other: com.ajsnarr.linknotes.data.EntryContent) : EntryContent {
            return EntryContent(content = other.value)
        }
    }

    override fun toAppObject(): com.ajsnarr.linknotes.data.EntryContent {
        return com.ajsnarr.linknotes.data.EntryContent(value=this.content!!)
    }
}
