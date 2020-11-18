package com.ajsnarr.linknotes.notes

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider
import com.ajsnarr.linknotes.data.Entry
import com.ajsnarr.linknotes.data.Note

/**
 * If passed in inNote is null, creates a new inNote.
 */
class EditNoteViewModel(inNote: Note?) : ViewModel() {

    val note: Note = inNote ?: Note.newEmpty()

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
    class Factory(val inNote: Note?) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(EditNoteViewModel::class.java)) {
                EditNoteViewModel(inNote) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}
