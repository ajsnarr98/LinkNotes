package com.github.ajsnarr98.linknotes.data.db

data class EntryContent(var content: String? = null)
    : DBObject<com.github.ajsnarr98.linknotes.data.EntryContent> {

    companion object {
        fun fromAppObject(other: com.github.ajsnarr98.linknotes.data.EntryContent) : EntryContent {
            return EntryContent(content = other.value)
        }
    }

    override fun toAppObject(): com.github.ajsnarr98.linknotes.data.EntryContent {
        return com.github.ajsnarr98.linknotes.data.EntryContent(value=this.content!!)
    }
}
