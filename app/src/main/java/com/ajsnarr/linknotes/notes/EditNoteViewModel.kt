package com.ajsnarr.linknotes.notes

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider
import com.ajsnarr.linknotes.BaseViewModel
import com.ajsnarr.linknotes.data.Entry
import com.ajsnarr.linknotes.data.Note
import com.ajsnarr.linknotes.data.NoteCollection
import com.ajsnarr.linknotes.data.UUID
import java.lang.IllegalStateException

/**
 * If passed in noteID is null, creates a new note.
 */
class EditNoteViewModel(noteID: UUID?) : BaseViewModel() {

    val note: Note = notesCollection.findByID(noteID) ?: Note.newEmpty()

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

    fun updateExistingEntry(updated: Entry): Boolean {
        return note.updateExistingEntry(updated)
    }

    fun deleteEntry(entry: Entry) {
        note.deleteEntry(entry)
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
