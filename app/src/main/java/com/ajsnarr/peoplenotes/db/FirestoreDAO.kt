package com.ajsnarr.peoplenotes.db

import com.google.firebase.firestore.*
import timber.log.Timber
import java.lang.Exception

/**
 * Used for pulling notes and related info out of the DB.
 */
class FirestoreDAO {

    companion object {
        const val NOTES_COLLECTION: String = "notes"

        val instance = FirestoreDAO()
    }

    private val db = FirebaseFirestore.getInstance()
    private var notesListenerReg: ListenerRegistration? = null

    /**
     * Gets all notes stored in the db.
     */
    fun getAllNotes(onSuccess: (Note) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection(NOTES_COLLECTION)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    onSuccess(document.toObject(Note::class.java))
                }
            }
            .addOnFailureListener(onFailure)
    }

    /**
     * Firestore-specific
     *
     * Adds a listener for changes in different notes. Only one listener at a
     * time. Adding a listener will remove the old one.
     */
    fun addNotesChangeListener(listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit) {
        removeNotesChangeListener() // remove if exists
        notesListenerReg = db.collection(NOTES_COLLECTION).addSnapshotListener(listener)
    }

    /**
     * Firestore-specific
     *
     * Removes the current listener for changes in different notes.
     *
     * @return true if there was a listener to remove, false otherwise
     */
    fun removeNotesChangeListener(): Boolean {
        notesListenerReg?.remove()
        val removed = notesListenerReg != null

        if (removed) { notesListenerReg = null }

        return removed
    }

    /**
     * Updates an existing note or inserts this one if it does not exist.
     */
    fun upsertNote(note: Note) {

        // generate a new document if neccesary
        val documentRef: DocumentReference
                = if (note.isNewNote()) {
                    db.collection(NOTES_COLLECTION)
                        .document()
                } else {
                    // new document without id
                    db.collection(NOTES_COLLECTION)
                        .document(note.id!!)
        }

        val savedNote: Note
                = if (note.isNewNote()) {
                    // add new id to note
                    note.withId(documentRef.id)
                } else {
                    note
                }

        documentRef.set(savedNote).apply {
            addOnSuccessListener { Timber.d("Successfully upserted note") }
            addOnFailureListener { err -> Timber.e("Failed to upsert note: $err") }
        }
    }

    /**
     * Deletes a note.
     */
    fun deleteNote(note: Note) {
        if (note.isNewNote() == false) {
            db.collection(NOTES_COLLECTION)
                .document(note.id!!)
                .delete()
                .addOnSuccessListener { Timber.i("Note ${note.id} ${note.name} successfully deleted") }
                .addOnFailureListener { Timber.e("Note ${note.id} ${note.name} successfully deleted") }
        }
    }

    /**
     * Deletes all given notes
     */
    fun deleteNotes(notes: Collection<Note>) {
        for (note in notes) {
            deleteNote(note)
        }
    }
}