package com.ajsnarr.peoplenotes.data.db

data class EntryType(var type: String? = null):
    DBObject<com.ajsnarr.peoplenotes.data.EntryType> {

    companion object {
        fun fromAppObject(other: com.ajsnarr.peoplenotes.data.EntryType) : EntryType {
            return EntryType(type = other.type)
        }
    }

    override fun toAppObject(): com.ajsnarr.peoplenotes.data.EntryType {
        return com.ajsnarr.peoplenotes.data.EntryType(type=type!!)
    }
}
