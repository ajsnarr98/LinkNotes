package com.github.ajsnarr98.linknotes.data.db.firestore

import com.github.ajsnarr98.linknotes.data.Note
import com.github.ajsnarr98.linknotes.data.db.DBCollectionObject
import com.github.ajsnarr98.linknotes.util.isNotNullOrBlank
import java.util.*


data class DBNote(
    override val id: String? = null, // if ID is null or blank string (at most whitespace chars), creates a new id when it is inserted
    val type: String? = null,
    val name: String? = null,
    val dateCreated: Date? = null,
    val lastDateEdited: Date? = null,
    val nicknames: MutableList<String>? = null,
    val mainPicture: DBPicture? = null,
    val pictures: MutableList<DBPicture>? = null,
    val tags: MutableList<DBTag>? = null,
    val entries: MutableList<DBEntry>? = null,
    val notes: MutableList<DBNote>? = null
) : DBCollectionObject<Note> {

    companion object {
        fun fromAppObject(other: Note) : DBNote {
            return DBNote(
                id = other.id,
                type = other.type,
                name = other.name,
                dateCreated = other.dateCreated,
                lastDateEdited = other.lastDateEdited,
                nicknames = other.nicknames,
                mainPicture = if (other.mainPicture != null) DBPicture.fromAppObject(
                    other.mainPicture!!
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

    override fun toAppObject(): com.github.ajsnarr98.linknotes.data.Note {
        return Note(
            id = this.id!!,
            _type = this.type!!,
            _name = this.name!!,
            dateCreated = this.dateCreated!!,
            lastDateEdited = this.lastDateEdited!!,
            nicknames = this.nicknames!!,
            mainPicture = this.mainPicture?.url,
            pictures = this.pictures!!.map { it.url!! }.toMutableList(),
            tags = this.tags!!.map { it.toAppObject() }.toMutableSet(),
            entries = toAppEntryList(),
            notes = this.notes?.map { it.toAppObject() }?.toMutableList()
        )
    }

    private fun toAppEntryList(): com.github.ajsnarr98.linknotes.data.EntryList {
        return com.github.ajsnarr98.linknotes.data.EntryList.fromCollection(
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
