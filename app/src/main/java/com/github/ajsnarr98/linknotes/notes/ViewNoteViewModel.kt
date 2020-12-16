package com.github.ajsnarr98.linknotes.notes

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.ajsnarr98.linknotes.BaseViewModel
import com.github.ajsnarr98.linknotes.data.Note
import com.github.ajsnarr98.linknotes.data.UUID
import java.lang.IllegalStateException

class ViewNoteViewModel(private val noteId: UUID): BaseViewModel(), DefaultLifecycleObserver {

    var note: Note = notesCollection.findByID(noteId) ?: throw IllegalStateException("Invalid note ID provided. $noteId")

    override val lifecycleObservers: MutableCollection<LifecycleObserver>
        get() = super.lifecycleObservers.also { it.add(this) } // add this as a LifecycleObserver

    override fun onStart(owner: LifecycleOwner) {
        // update note when lifecycleOwner loads
        note = notesCollection.findByID(noteId) ?: throw IllegalStateException("Invalid note ID provided. $noteId")
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