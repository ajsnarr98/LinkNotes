package com.ajsnarr.peoplenotes.data.db

import com.ajsnarr.peoplenotes.data.NoteCollection
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
        // get notes from db
        dao.getAllNotes(
            onSuccess = {note -> this.safeAdd(note); Timber.v("Received note ${note.id} from database")},
            onFailure = {err  -> Timber.e("Error getting note from db: $err")}
        )
    }

    /**
     * Add a listener for updating notes based on remote changes.
     */
    override fun onActivityCreate() {
        dao.addNotesChangeListener { snapshots, firebaseFirestoreException ->
            if (snapshots?.documentChanges == null) return@addNotesChangeListener

            Timber.i("Remote changes received in note collection")

            for (dc in snapshots.documentChanges) {
                val note = dc.document.toObject(Note::class.java)
                when (dc.type) {
                    DocumentChange.Type.ADDED    -> this.safeAdd(note)
                    DocumentChange.Type.REMOVED  -> this.safeRemove(note)
                    DocumentChange.Type.MODIFIED -> {
                        if (this.contains(note.toAppObject())) {
                            this.safeRemove(note)
                            this.safeAdd(note)
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
    override fun onActivityDestroy() {
        dao.removeNotesChangeListener()
    }


    override fun generateNewUUID(newNote: com.ajsnarr.peoplenotes.data.Note): String {
        // this method runs right before add is called on this Note, so
        // upsert note here
        return dao.upsertNote(Note.fromAppObject(newNote))
    }

    /**
     * Adds a note to set without updating the database.
     */
    private fun safeAdd(element: Note) {
        super.add(element.toAppObject())
    }

    /**
     * Removes a note from set without updating the database.
     */
    private fun safeRemove(element: Note) {
        super.remove(element.toAppObject())
    }

    // inherit mutable set methods (and configure to update db)

    override fun add(element: com.ajsnarr.peoplenotes.data.Note): Boolean {
        return super.add(element).also {
            // knowing generating a newUUID will upsert, only upsert if element
            // is not a new note
            if (element.isNewNote() == false) dao.upsertNote(
                Note.fromAppObject(
                    element
                )
            )
        }
    }
    override fun clear() {
        super.clear()
        dao.deleteNotes(this.map { note -> Note.fromAppObject(note) })
    }
    override fun remove(element: com.ajsnarr.peoplenotes.data.Note): Boolean {
        return super.remove(element).also {
            dao.deleteNote(Note.fromAppObject(element))
        }
    }
    override fun removeAll(elements: Collection<com.ajsnarr.peoplenotes.data.Note>): Boolean {
        return super.removeAll(elements).also {
            dao.deleteNotes(elements.map { note -> Note.fromAppObject(note) })
        }
    }
    override fun retainAll(elements: Collection<com.ajsnarr.peoplenotes.data.Note>): Boolean {
        // give dao the inverse of elements being kept in the set, to remove
        val removeSet = this.filter { note -> !elements.contains(note) }
        return super.retainAll(elements).also {
            dao.deleteNotes(removeSet.map { note -> Note.fromAppObject(note) })
        }
    }
}
