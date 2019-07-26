package com.ajsnarr.peoplenotes.data

data class EntryType(var type: String): DataObject<com.ajsnarr.peoplenotes.db.EntryType> {
    companion object {
        val EMPTY get() = EntryType("none")

        fun fromDBObj(other: com.ajsnarr.peoplenotes.db.EntryType): EntryType {
            return EntryType(type = other.type!!)
        }
    }

    override fun toDBObject(): com.ajsnarr.peoplenotes.db.EntryType {
        return com.ajsnarr.peoplenotes.db.EntryType(type=type)
    }
}