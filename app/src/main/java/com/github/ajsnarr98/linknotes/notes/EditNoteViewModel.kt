package com.github.ajsnarr98.linknotes.notes

import androidx.lifecycle.*
import com.github.ajsnarr98.linknotes.Providers
import com.github.ajsnarr98.linknotes.data.Entry
import com.github.ajsnarr98.linknotes.data.Note
import com.github.ajsnarr98.linknotes.data.NoteCollection
import com.github.ajsnarr98.linknotes.data.Tag
import com.github.ajsnarr98.linknotes.data.TagCollection
import com.github.ajsnarr98.linknotes.data.UUID
import java.util.*
import kotlin.NoSuchElementException

/**
 * If passed in noteID is null, creates a new note.
 */
class EditNoteViewModel(noteID: UUID?, hasUnsavedChanges: Boolean): ViewModel() {

    companion object {
        // used for undo/redo
        private const val MAX_STORED_CHANGES = 50
    }

    val notesCollection: NoteCollection = Providers.noteCollection!!
    private val tagCollection: TagCollection = Providers.tagCollection!!
    private val unsavedChangeStore = Providers.unsavedChangeStore

    /**
     * All lifecycle observers known by this ViewModel.
     */
    val lifecycleObservers: MutableCollection<LifecycleObserver>
        get() = arrayListOf(notesCollection, tagCollection)

    // grab a copy of the existing note (to modify) or create a new empty note
    var note: Note = if (hasUnsavedChanges) {
        requireNotNull(unsavedChangeStore?.getNote(), { "Unsaved changes not found" })
    } else {
        notesCollection.findByID(noteID)?.copy() ?: Note.newEmpty()
    }

    private val undoStack: Deque<Note> = LinkedList()
    private val redoStack: Deque<Note> = LinkedList()

    private val mutableCanUndo = MutableLiveData(false)
    private val mutableCanRedo = MutableLiveData(false)

    val canUndo: LiveData<Boolean>
        get() = mutableCanUndo
    val canRedo: LiveData<Boolean>
        get() = mutableCanRedo

    val entries: MutableList<Entry>
        get() = note.entries
    var title: String
        get() = note.name
        set(value) { note.name = value }
    var noteType: String
        get() = note.type
        set(value) { note.type = value }

    val hasMadeChanges: Boolean
        get() = undoStack.isNotEmpty()

    fun addNewEntry() {
        onChangeMade()
        note.addNewEntry()
        onPostChangeMade()
    }

    /**
     * Adds tags only if there isn't a matching tag in the note.
     */
    fun addTags(tags: Collection<Tag>) {
        onChangeMade()
        note.tags.addAll(tags)
        onPostChangeMade()
    }

    /**
     * Sets this notes tags to contain only the tags given.
     */
    fun setTags(tags: Collection<Tag>) {
        onChangeMade()
        note.tags.clear()
        note.tags.addAll(tags)
        onPostChangeMade()
    }

    /**
     * Tries to remove a tag from this note.
     */
    fun removeTag(tag: Tag) {
        onChangeMade()
        note.tags.remove(tag)
        onPostChangeMade()
    }

    fun updateExistingEntry(updated: Entry): Boolean {
        onChangeMade()
        return note.updateExistingEntry(updated).also {
            onPostChangeMade()
        }
    }

    fun deleteEntry(entry: Entry) {
        onChangeMade()
        note.deleteEntry(entry)
        onPostChangeMade()
    }

    /**
     * Redoes an undo.
     */
    fun redo() {
        try {
            val change = redoStack.pop()
            undoStack.push(this.note.copy())
            if (canUndo.value == false) mutableCanUndo.value = true
            if (redoStack.isEmpty()) mutableCanRedo.value = false
            this.note = change
        } catch (_: NoSuchElementException) {}
    }

    /**
     * Undoes a change.
     */
    fun undo() {
        try {
            val change = undoStack.pop()
            redoStack.push(this.note.copy())
            if (canRedo.value == false) mutableCanRedo.value = true
            if (undoStack.isEmpty()) mutableCanUndo.value = false
            this.note = change
        } catch (_: NoSuchElementException) {}
    }

    /**
     * Saves the stored note to the db.
     */
    fun saveNote() {
        val toSave = this.note.copy()
        toSave.fillDefaults()
        notesCollection.add(toSave)
        undoStack.clear()
        unsavedChangeStore?.clearNote()
    }

    /**
     * Call when ready to cancel changes to this note.
     */
    fun onCancel() {
        unsavedChangeStore?.clearNote()
    }

    /**
     * Call this right before the change is made.
     */
    private fun onChangeMade() {
        if (!redoStack.isEmpty()) {
            redoStack.clear()
            mutableCanRedo.value = false
        }
        undoStack.push(note.copy())
        if (canUndo.value == false) {
            mutableCanUndo.value = true
        }
        if (undoStack.size > MAX_STORED_CHANGES) {
            undoStack.removeLast()
        }
    }

    /**
     * Call this after a change is made.
     */
    private fun onPostChangeMade() {
        // TODO - make this only get called after X interval, and on the IO thread
        unsavedChangeStore?.persistNote(note)
    }

    /**
     * If passed in inNote is null, creates a new note.
     */
    class Factory(private val inNoteID: UUID?, private val hasUnsavedChanges: Boolean = false) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(EditNoteViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                EditNoteViewModel(inNoteID, hasUnsavedChanges) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}
