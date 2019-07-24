package com.ajsnarr.peoplenotes.db

import com.google.firebase.firestore.DocumentChange
import timber.log.Timber

/**
 * A notes collection that works with firestore.
 *
 * MUST clear listeners via call to onActivityEnd.
 */
class FirestoreNoteCollection : NoteCollection() {

    private val dao = FirestoreDAO.instance

    init {
        if (this.value == null) {
            this.value = mutableSetOf<Note>()
        }

        // get notes from db
        dao.getAllNotes(
            onSuccess = {note -> this.add(note); Timber.v("Received note ${note.id} from database")},
            onFailure = {err  -> Timber.e("Error getting note from db: $err")}
        )
    }

    /**
     * Add a listener for updating notes based on remote changes.
     */
    override fun onActivityStart() {
        dao.addNotesChangeListener { snapshots, firebaseFirestoreException ->
            if (snapshots?.documentChanges == null) return@addNotesChangeListener

            Timber.d("Remote changes received in note collection")

            for (dc in snapshots.documentChanges) {
                val note = dc.document.toObject(Note::class.java)
                when (dc.type) {
                    DocumentChange.Type.ADDED    -> this.value?.add(note)
                    DocumentChange.Type.REMOVED  -> this.value?.remove(note)
                    DocumentChange.Type.MODIFIED -> {
                        if (this.value?.contains(note) == true) {
                            this.value?.remove(note)
                            this.value?.add(note)
                        }
                    }
                    else                         -> return@addNotesChangeListener
                }
            }
            // update based on current changes
            update()
        }
    }

    /**
     * Remove listener at end of activity.
     */
    override fun onActivityStop() {
        dao.removeNotesChangeListener()
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