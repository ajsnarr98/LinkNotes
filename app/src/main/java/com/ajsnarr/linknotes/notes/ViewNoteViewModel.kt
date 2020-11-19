package com.ajsnarr.linknotes.notes

import androidx.lifecycle.*
import com.ajsnarr.linknotes.BaseViewModel
import com.ajsnarr.linknotes.data.Note
import com.ajsnarr.linknotes.data.UUID
import java.lang.IllegalStateException

class ViewNoteViewModel(private val noteId: UUID): BaseViewModel(), DefaultLifecycleObserver {

    var note: Note = notesCollection.findByID(noteId) ?: throw IllegalStateException("Invalid note ID provided.")

    override val lifecycleObservers: MutableCollection<LifecycleObserver>
        get() = super.lifecycleObservers.also { it.add(this) } // add this as a LifecycleObserver

    override fun onStart(owner: LifecycleOwner) {
        // update note when lifecycleOwner loads
        note = notesCollection.findByID(noteId) ?: throw IllegalStateException("Invalid note ID provided.")
    }

    /**
     * Unlike EditNoteViewModel, passed in note cannot be null.
     */
    class Factory(private val noteId: UUID) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ViewNoteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                ViewNoteViewModel(noteId) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}