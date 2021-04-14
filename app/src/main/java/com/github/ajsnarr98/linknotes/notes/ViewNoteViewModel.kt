package com.github.ajsnarr98.linknotes.notes

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.ajsnarr98.linknotes.Providers
import com.github.ajsnarr98.linknotes.data.Note
import com.github.ajsnarr98.linknotes.data.NoteCollection
import com.github.ajsnarr98.linknotes.data.TagCollection
import com.github.ajsnarr98.linknotes.data.UUID
import java.lang.IllegalStateException

class ViewNoteViewModel(private val noteId: UUID): ViewModel(), DefaultLifecycleObserver {

    private val notesCollection: NoteCollection = Providers.noteCollection!!
    private val tagCollection: TagCollection = Providers.tagCollection!!

    /**
     * All lifecycle observers known by this ViewModel.
     */
    val lifecycleObservers: MutableCollection<LifecycleObserver>
        get() = arrayListOf(notesCollection, tagCollection, this)

    private var _note: Lazy<Note> = lazy {
        notesCollection.findByID(noteId) ?: throw IllegalStateException("Invalid note ID provided. $noteId")
    }
    var note: Note
        get() = _note.value
        set(value) { _note = lazy { value } }


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