package com.ajsnarr.peoplenotes.db

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
    override fun onActivityStart() {
        dao.addNotesChangeListener { snapshots, firebaseFirestoreException ->
            if (snapshots?.documentChanges == null) return@addNotesChangeListener

            Timber.i("Remote changes received in note collection")

            for (dc in snapshots.documentChanges) {
                val dbNote = dc.document.toObject(Note::class.java)
                val note = com.ajsnarr.peoplenotes.data.Note.fromDBNote(dbNote)
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


    override fun generateNewUUID(newNote: com.ajsnarr.peoplenotes.data.Note): String {
        // this method runs right before add is called on this Note, so
        // upsert note here
        return dao.upsertNote(newNote.toDBObject())
    }

    /**
     * Adds a note to set without updating the database.
     */
    private fun safeAdd(element: Note) {
        super.add(com.ajsnarr.peoplenotes.data.Note.fromDBNote(element))
    }

    // inherit mutable set methods (and configure to update db)

    override fun add(element: com.ajsnarr.peoplenotes.data.Note): Boolean {
        return super.add(element).also {
            // knowing generating a newUUID will upsert, only upsert if element
            // is not a new note
            if (element.isNewNote() == false) dao.upsertNote(element.toDBObject())
        }
    }
    override fun clear() {
        super.clear()
        dao.deleteNotes(this.map { note -> note.toDBObject() })
    }
    override fun remove(element: com.ajsnarr.peoplenotes.data.Note): Boolean {
        return super.remove(element).also {
            dao.deleteNote(element.toDBObject())
        }
    }
    override fun removeAll(elements: Collection<com.ajsnarr.peoplenotes.data.Note>): Boolean {
        return super.removeAll(elements).also {
            dao.deleteNotes(elements.map { note -> note.toDBObject() })
        }
    }
    override fun retainAll(elements: Collection<com.ajsnarr.peoplenotes.data.Note>): Boolean {
        // give dao the inverse of elements being kept in the set, to remove
        dao.deleteNotes(
            mutableSetOf<Note>(
                *this.apply { removeAll(elements) }
                    .map { note -> note.toDBObject() }
                    .toTypedArray()
            )
        )
        return super.retainAll(elements)
    }
}