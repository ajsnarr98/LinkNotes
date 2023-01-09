package com.github.ajsnarr98.linknotes.network.storage.firestore

import com.github.ajsnarr98.linknotes.network.domain.Note
import com.github.ajsnarr98.linknotes.network.storage.DBCollectionObject
import com.github.ajsnarr98.linknotes.network.domain.EntryList
import java.util.*


data class DBNote(
    override val id: String? = null, // if ID is null or blank string (at most whitespace chars), creates a new id when it is inserted
    val type: String? = null,
    val name: String? = null,
    val timeCreated: Date? = null,
    val lastTimeEdited: Date? = null,
    val nicknames: List<String>? = null,
    val mainPicture: DBPicture? = null,
    val pictures: List<DBPicture>? = null,
    val tags: List<DBTag>? = null,
    val entries: List<DBEntry>? = null,
    val notes: List<DBNote>? = null
) : DBCollectionObject<Note> {

    companion object {
        fun fromAppObject(other: Note) : DBNote {
            return DBNote(
                id = other.id,
                type = other.type,
                name = other.name,
                timeCreated = other.timeCreated,
                lastTimeEdited = other.lastTimeEdited,
                nicknames = other.nicknames,
                mainPicture = if (other.mainPicture != null) DBPicture.fromAppObject(
                    other.mainPicture
                ) else null,
                pictures = other.pictures.map { url ->
                    DBPicture.fromAppObject(
                        url
                    )
                }.toMutableList(),
                tags = other.tags.map { tag -> DBTag.fromAppObject(tag) }.toMutableList(),
                entries = other.entries.map { entry ->
                    DBEntry.fromAppObject(
                        entry
                    )
                }.toMutableList(),
                notes = other.notes?.map { note -> fromAppObject(note) }?.toMutableList()
            )
        }
    }

    override val readableLogName: String = "Note '$id' -> name: $name"

    /**
     * Returns a new note with the given ID.
     */
    override fun withID(id: String): DBNote {
        return this.copy(id=id)
    }

    override fun toAppObject(): Note {
        return Note(
            id = this.id!!,
            type = this.type!!,
            name = this.name!!,
            timeCreated = this.timeCreated!!,
            lastTimeEdited = this.lastTimeEdited!!,
            nicknames = this.nicknames!!,
            mainPicture = this.mainPicture?.url,
            pictures = this.pictures!!.map { it.url!! }.toMutableList(),
            tags = this.tags!!.map { it.toAppObject() }.toMutableSet(),
            entries = toAppEntryList(),
            notes = this.notes?.map { it.toAppObject() }?.toMutableList()
        )
    }

    private fun toAppEntryList(): EntryList {
        return EntryList.fromCollection(
            entries?.map { it.toAppObject() } ?: listOf()
        )
    }

    /**
     *  Two notes are equal if they have the same id.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DBNote

        if (id != other.id) return false

        return true
    }

    /**
     *  Two notes are equal if they have the same id.
     */
    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
