package com.github.ajsnarr98.linknotes.notes

import androidx.lifecycle.*
import com.github.ajsnarr98.linknotes.BaseViewModel
import com.github.ajsnarr98.linknotes.data.Entry
import com.github.ajsnarr98.linknotes.data.Note
import com.github.ajsnarr98.linknotes.data.Tag
import com.github.ajsnarr98.linknotes.data.UUID

/**
 * If passed in noteID is null, creates a new note.
 */
class EditNoteViewModel(noteID: UUID?) : BaseViewModel() {

    // grab a copy of the existing note (to modify) or create a new empty note
    val note: Note = notesCollection.findByID(noteID)?.copy() ?: Note.newEmpty()

    val entries: MutableList<Entry>
        get() = note.entries
    var title: String
        get() = note.name
        set(value) { note.name = value }
    var noteType: String
        get() = note.type
        set(value) { note.type = value }

    fun addNewEntry() {
        note.addNewEntry()
    }

    /**
     * Adds tags only if there isn't a matching tag in the note.
     */
    fun addTags(tags: Collection<Tag>) {
        note.tags.addAll(tags)
    }

    /**
     * Tries to remove a tag from this note.
     */
    fun removeTag(tag: Tag) {
        note.tags.remove(tag)
    }

    fun updateExistingEntry(updated: Entry): Boolean {
        return note.updateExistingEntry(updated)
    }

    fun deleteEntry(entry: Entry) {
        note.deleteEntry(entry)
    }

    /**
     * Saves the stored note to the db.
     */
    fun saveNote() {
        val toSave = this.note.copy()
        toSave.fillDefaults()
        notesCollection.add(toSave)
    }

    /**
     * If passed in inNote is null, creates a new note.
     */
    class Factory(private val inNoteID: UUID?) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(EditNoteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                EditNoteViewModel(inNoteID) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}
