package com.ajsnarr.peoplenotes.db

import com.google.firebase.firestore.DocumentChange
import kotlinx.android.parcel.Parcelize
import timber.log.Timber

/**
 * A notes collection that works with firestore.
 *
 * MUST clear listeners via call to onActivityEnd.
 */
class FirestoreNoteCollection : NoteCollection() {

    private val dao = FirestoreDAO.instance

    init {
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


    // inherit mutable set methods (and set to update db)
    override fun add(element: Note): Boolean {
        return super.add(element).also {
            dao.upsertNote(element)
        }
    }
    override fun clear() {
        super.clear()
        dao.deleteNotes(this)
    }
    override fun remove(element: Note): Boolean {
        return super.remove(element).also {
            dao.deleteNote(element)
        }
    }
    override fun removeAll(elements: Collection<Note>): Boolean {
        return super.removeAll(elements).also {
            dao.deleteNotes(elements)
        }
    }
    override fun retainAll(elements: Collection<Note>): Boolean {
        // give dao the inverse of elements being kept in the set, to remove
        dao.deleteNotes(mutableSetOf(*this.toTypedArray()).apply {removeAll(elements)})
        return super.retainAll(elements)
    }
}