package com.github.ajsnarr98.linknotes.data.db

import androidx.lifecycle.LifecycleOwner
import com.github.ajsnarr98.linknotes.data.NoteCollection
import com.github.ajsnarr98.linknotes.data.UUID
import com.google.firebase.firestore.DocumentChange
import timber.log.Timber

/**
 * A notes collection that works with firestore.
 *
 * MUST clear listeners via call to onActivityEnd.
 */
class FirestoreNoteCollection(private val dao: NotesDAO = DBInstances.notesDAO) : NoteCollection() {

    init {
        // get notes from db
        dao.getAll(
            onSuccess = {note: Note -> this.safeAdd(note); Timber.v("Received note ${note.id} from database")},
            onFailure = {err  -> Timber.e("Error getting note from db: $err")}
        )
    }

    /**
     * Add a listener for updating notes based on remote changes.
     */
    override fun onStart(owner: LifecycleOwner) {
        if (dao is FirestoreChangeListenerHolder) {
            dao.setChangeListener { snapshots, firebaseFirestoreException ->
                if (snapshots?.documentChanges == null) return@setChangeListener

                Timber.i("Remote changes received in note collection")

                for (dc in snapshots.documentChanges) {
                    val note = dc.document.toObject(Note::class.java)
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> this.safeAdd(note)
                        DocumentChange.Type.REMOVED -> this.safeRemove(note)
                        DocumentChange.Type.MODIFIED -> {
                            if (this.contains(note.toAppObject())) {
                                this.safeRemove(note)
                                this.safeAdd(note)
                            }
                        }
                        else -> return@setChangeListener
                    }
                }
                // update based on current changes
                update()
            }
        } else {
            Timber.e("Unexpected behavior encountered. DAO should be a FirestoreChangeListenerHolder")
        }
    }

    /**
     * Remove listener at end of activity.
     */
    override fun onStop(owner: LifecycleOwner) {
        if (dao is FirestoreChangeListenerHolder) {
            dao.removeChangeListener()
        } else {
            Timber.e("Unexpected behavior encountered. DAO should be a FirestoreChangeListenerHolder")
        }
    }


    override fun generateNewUUID(newNote: com.github.ajsnarr98.linknotes.data.Note): UUID {
        // this method runs right before add is called on this Note, so
        // upsert note here
        return dao.upsert(Note.fromAppObject(newNote))
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

    override fun add(element: com.github.ajsnarr98.linknotes.data.Note): Boolean {
        return super.add(element).also {
            // knowing generating a newUUID will upsert, only upsert if element
            // is not a new note
            if (element.isNewNote() == false) dao.upsert(
                Note.fromAppObject(
                    element
                )
            )
        }
    }
    override fun clear() {
        val toDelete = this.map { note -> Note.fromAppObject(note) }
        super.clear()
        dao.deleteAll(toDelete)
    }
    override fun remove(element: com.github.ajsnarr98.linknotes.data.Note): Boolean {
        return super.remove(element).also {
            dao.delete(Note.fromAppObject(element))
        }
    }
    override fun removeAll(elements: Collection<com.github.ajsnarr98.linknotes.data.Note>): Boolean {
        return super.removeAll(elements).also {
            dao.deleteAll(elements.map { note -> Note.fromAppObject(note) })
        }
    }
    override fun retainAll(elements: Collection<com.github.ajsnarr98.linknotes.data.Note>): Boolean {
        // give dao the inverse of elements being kept in the set, to remove
        val removeSet = this.filter { note -> !elements.contains(note) }
        return super.retainAll(elements).also {
            dao.deleteAll(removeSet.map { note -> Note.fromAppObject(note) })
        }
    }
}
