package com.ajsnarr.linknotes.data.db

data class EntryType(var type: String? = null):
    DBObject<com.ajsnarr.linknotes.data.EntryType> {

    companion object {
        fun fromAppObject(other: com.ajsnarr.linknotes.data.EntryType) : EntryType {
            return EntryType(type = other.value)
        }
    }

    override fun toAppObject(): com.ajsnarr.linknotes.data.EntryType {
        return com.ajsnarr.linknotes.data.EntryType(value=type!!)
    }
}
