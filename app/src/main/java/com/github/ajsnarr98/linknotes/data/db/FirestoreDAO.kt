package com.github.ajsnarr98.linknotes.data.db

import com.google.firebase.firestore.*
import java.lang.Exception

/**
 * Used for pulling notes and related info out of the DB.
 */
interface FirestoreDAO {

    companion object {
        val instance: FirestoreDAO = FirestoreDAOImpl()
    }

    /**
     * Gets all notes stored in the db.
     */
    fun getAllNotes(onSuccess: (Note) -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Gets all tag trees stored in the db.
     */
    fun getAllTags(onSuccess: (TagTree) -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Firestore-specific
     *
     * Adds a listener for changes in different notes. Only one listener at a
     * time. Adding a listener will remove the old one.
     */
    fun addNotesChangeListener(listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit)

    /**
     * Firestore-specific
     *
     * Removes the current listener for changes in different notes.
     *
     * @return true if there was a listener to remove, false otherwise
     */
    fun removeNotesChangeListener(): Boolean

    /**
     * Firestore-specific
     *
     * Adds a listener for changes in different tag trees. Only one listener at
     * a time. Adding a listener will remove the old one.
     */
    fun addTagsChangeListener(listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit)

    /**
     * Firestore-specific
     *
     * Removes the current listener for changes in different tag trees.
     *
     * @return true if there was a listener to remove, false otherwise
     */
    fun removeTagsChangeListener(): Boolean

    /**
     * Updates an existing note or inserts this one if it does not exist.
     *
     * Returns the note's UUID.
     */
    fun upsertNote(note: Note): String

    /**
     * Deletes a note.
     */
    fun deleteNote(note: Note)

    /**
     * Deletes all given notes
     */
    fun deleteNotes(notes: Collection<Note>)

    /**
     * Updates an existing tag tree or inserts this one if it does not exist.
     *
     * Returns the tree's UUID.
     */
    fun upsertTagTree(tags: TagTree): String

    /**
     * Deletes given tag tree.
     */
    fun deleteTagTree(tags: TagTree)

    /**
     * Deletes all given tag trees.
     */
    fun deleteTagTrees(tags: Collection<TagTree>)
}
