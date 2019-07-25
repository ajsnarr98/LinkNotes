package com.ajsnarr.peoplenotes.db

import androidx.lifecycle.LiveData
import timber.log.Timber

/**
 * Represents a collection of notes from the DB. Updates using livedata.
 */
abstract class NoteCollection : LiveData<MutableSet<Note>>(), MutableSet<Note> {

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
     * Called when the activity starts.
     */
    abstract fun onActivityStart()

    /**
     * Called when the activity stops.
     */
    abstract fun onActivityStop()

    /**
     * Sets LiveData value to cause an event.
     */
    protected fun update() {
        this.value = this.value
    }


    // inherit set methods
    override val size: Int get() = this.value?.size ?: 0
    override fun contains(element: Note): Boolean = this.value?.contains(element) ?: false
    override fun containsAll(elements: Collection<Note>): Boolean = this.value?.containsAll(elements) ?: false
    override fun isEmpty(): Boolean = this.value?.isEmpty() ?: true
    override fun iterator(): MutableIterator<Note> = this.value?.iterator() ?: mutableSetOf<Note>().iterator()

    // inherit mutable set methods
    override fun add(element: Note): Boolean = this.value?.add(element).also { update() } ?: false
    override fun addAll(elements: Collection<Note>): Boolean = this.value?.addAll(elements).also { update() } ?: false
    override fun clear() { this.value?.clear().also { update() } }
    override fun remove(element: Note): Boolean = this.value?.remove(element).also { update() } ?: false
    override fun removeAll(elements: Collection<Note>): Boolean = this.value?.removeAll(elements).also { update() } ?: false
    override fun retainAll(elements: Collection<Note>): Boolean = this.value?.retainAll(elements).also { update() } ?: false
}