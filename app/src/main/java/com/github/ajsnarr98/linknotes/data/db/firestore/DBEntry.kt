package com.github.ajsnarr98.linknotes.data.db.firestore

import com.github.ajsnarr98.linknotes.data.db.DBObject
import java.util.*

data class DBEntry(
    val id: String? = null,
    val type: DBEntryType? = null,
    val content: DBEntryContent? = null,
    val dateCreated: Date? = null,
    val lastDateEdited: Date? = null,
    val deletable: Boolean? = null,
    val subEntries: MutableList<DBEntry>? = null
): DBObject<com.github.ajsnarr98.linknotes.data.Entry> {

    companion object {
        /**
         * Returns an entry from the database.
         */
        fun fromAppObject(other: com.github.ajsnarr98.linknotes.data.Entry): DBEntry {
            return DBEntry(
                id = other.id,
                type = DBEntryType.fromAppObject(other.type),
                content = DBEntryContent.fromAppObject(other.content),
                dateCreated = other.dateCreated,
                lastDateEdited = other.lastDateEdited,
                deletable = other.isDeletable,
                subEntries = other.subEntries.map { fromAppObject(it) }.toMutableList()
            )
        }
    }

    override fun toAppObject(): com.github.ajsnarr98.linknotes.data.Entry {
        return com.github.ajsnarr98.linknotes.data.Entry(
            id = this.id!!,
            type = this.type!!.toAppObject(),
            mContent = this.content!!.toAppObject(),
            dateCreated = this.dateCreated!!,
            lastDateEdited = this.lastDateEdited!!,
            isDeletable = this.deletable ?: true,
            subEntries = this.subEntries!!.map { it.toAppObject() }.toMutableList()
        )
    }
}
