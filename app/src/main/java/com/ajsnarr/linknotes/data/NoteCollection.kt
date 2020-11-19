package com.ajsnarr.linknotes.data

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import com.ajsnarr.linknotes.data.db.FirestoreNoteCollection
import timber.log.Timber

/**
 * Represents a collection of notes from the DB. Updates using livedata.
 *
 * DO NOT modify the value field of this class.
 */
abstract class NoteCollection : LiveData<MutableSet<Note>>(), MutableSet<Note>, DefaultLifecycleObserver {

    init {
        if (this.value == null) {
            this.value = mutableSetOf<Note>()
        }

        Timber.i("Created note collection")
    }

    companion object {
        val instance = FirestoreNoteCollection()
    }

    /**
     * Gets a new UUID to use with the note.
     *
     * Guaranteed to run iff add() is called and note is a newNote.
     */
    protected abstract fun generateNewUUID(newNote: Note): UUID

    /**
     * Sets LiveData value to cause an event.
     */
    protected fun update() {
        this.value = this.value
    }

    fun findByID(id: UUID?): Note? =
        if (id != null) this.find { note -> note.id == id } else null


    // inherit set methods
    override val size: Int get() = this.value?.size ?: 0
    override fun contains(element: Note): Boolean = this.value?.contains(element) ?: false
    override fun containsAll(elements: Collection<Note>): Boolean = this.value?.containsAll(elements) ?: false
    override fun isEmpty(): Boolean = this.value?.isEmpty() ?: true
    override fun iterator(): MutableIterator<Note> = this.value?.iterator() ?: mutableSetOf<Note>().iterator()

    // inherit mutable set methods
    override fun add(element: Note): Boolean {

        Timber.d("Adding note to note collection: $element")

        // a new note will need a valid id
        val note = if (element.isNewNote()) element.copy(id=generateNewUUID(element)) else element

        note.onSaveNote()

        this.value?.remove(note) // remove old if exists
        return this.value?.add(note).also { update() } ?: false
    }
    override fun addAll(elements: Collection<Note>): Boolean = elements.map { note -> this.add(note) }.any { it } // add all and return true if any were added
    override fun clear() { this.value?.clear().also { update() } }
    override fun remove(element: Note): Boolean = this.value?.remove(element).also { update() } ?: false
    override fun removeAll(elements: Collection<Note>): Boolean = this.value?.removeAll(elements).also { update() } ?: false
    override fun retainAll(elements: Collection<Note>): Boolean = this.value?.retainAll(elements).also { update() } ?: false
}
