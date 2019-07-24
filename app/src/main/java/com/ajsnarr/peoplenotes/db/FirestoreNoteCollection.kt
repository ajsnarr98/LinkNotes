package com.ajsnarr.peoplenotes.db

import android.util.Log

class FirestoreNoteCollection : NoteCollection() {

    val dao = FirestoreDAO.instance

    init {
        if (this.value == null) {
            this.value = mutableSetOf<Note>()
        }

        // get notes from db
        dao.getAllNotes(
            onSuccess = {note -> this.add(note) },
            onFailure = {err  -> /* Log here */}
        )
    }

    /**
     * Sets LiveData value to cause an event.
     */
    private fun update() {
        this.value = this.value
    }

    // inherit list methods
    override val size: Int get() = this.value?.size ?: 0
    override fun contains(element: Note): Boolean = this.value?.contains(element) ?: false
    override fun containsAll(elements: Collection<Note>): Boolean = this.value?.containsAll(elements) ?: false
    override fun isEmpty(): Boolean = this.value?.isEmpty() ?: true
    override fun iterator(): MutableIterator<Note> = this.value?.iterator() ?: mutableSetOf<Note>().iterator()

    // inherit mutable list methods
    override fun add(element: Note): Boolean = this.value?.add(element).also { update() } ?: false
    override fun addAll(elements: Collection<Note>): Boolean = this.value?.addAll(elements).also { update() } ?: false
    override fun clear() { this.value?.clear().also { update() } }
    override fun remove(element: Note): Boolean = this.value?.remove(element).also { update() } ?: false
    override fun removeAll(elements: Collection<Note>): Boolean = this.value?.removeAll(elements).also { update() } ?: false
    override fun retainAll(elements: Collection<Note>): Boolean = this.value?.retainAll(elements).also { update() } ?: false
}