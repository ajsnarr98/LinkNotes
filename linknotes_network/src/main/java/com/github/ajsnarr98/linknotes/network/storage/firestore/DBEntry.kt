package com.github.ajsnarr98.linknotes.network.storage.firestore

import com.github.ajsnarr98.linknotes.network.storage.DBObject
import com.github.ajsnarr98.linknotes.network.domain.Entry
import com.github.ajsnarr98.linknotes.network.domain.EntryContent
import com.github.ajsnarr98.linknotes.network.domain.EntryList
import com.github.ajsnarr98.linknotes.network.domain.EntryType
import java.util.*

data class DBEntry(
    val id: String? = null,
    val type: DBEntryType? = null,
    val content: DBEntryContent? = null,
    val priority: DBEntryPriority? = null,
    val timeCreated: Date? = null,
    val lastTimeEdited: Date? = null,
    val deletable: Boolean? = null,
    val subEntries: MutableList<DBEntry>? = null
): DBObject<Entry<*>> {

    companion object {
        /**
         * Returns an entry from the database.
         */
        fun fromAppObject(other: Entry<*>): DBEntry {
            return DBEntry(
                id = other.id,
                type = DBEntryType.fromAppObject(other.type),
                content = DBEntryContent.fromAppObject(other.content),
                priority = DBEntryPriority.fromAppObject(other.priority),
                timeCreated = other.timeCreated,
                lastTimeEdited = other.lastTimeEdited,
                deletable = other.isDeletable,
                subEntries = other.subEntries.map { fromAppObject(it) }.toMutableList()
            )
        }
    }

    override fun toAppObject(): Entry<*> {
        val id = this.id!!
        val type = this.type!!.toAppObject()
        val priority = this.priority!!.toAppObject()
        val timeCreated = this.timeCreated!!
        val lastTimeEdited = this.lastTimeEdited!!
        val isDeletable = this.deletable ?: true
        val subEntries = EntryList(this.subEntries!!.map { it.toAppObject() }.toMutableList())
        return when (type) {
            is EntryType.Default -> Entry.Default(
                id = id,
                content = this.content!!.toAppObject(),
                priority = priority,
                timeCreated = timeCreated,
                lastTimeEdited = lastTimeEdited,
                isDeletable = isDeletable,
                subEntries = subEntries,
            )
            is EntryType.Images -> Entry.Images(
                id = id,
                content = this.content!!.toAppObject() as EntryContent.Images,
                priority = priority,
                timeCreated = timeCreated,
                lastTimeEdited = lastTimeEdited,
                isDeletable = isDeletable,
                subEntries = subEntries,
            )
            is EntryType.Custom -> Entry.Custom(
                id = id,
                type = type,
                content = this.content!!.toAppObject(),
                priority = priority,
                timeCreated = timeCreated,
                lastTimeEdited = lastTimeEdited,
                isDeletable = isDeletable,
                subEntries = subEntries,
            )
        }
    }
}
