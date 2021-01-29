package com.github.ajsnarr98.linknotes.data.db

import com.google.firebase.firestore.*

/**
 * Adds specific
 */
interface FirestoreChangeListenerHolder {

    /**
     * Firestore-specific
     *
     * Adds a listener for changes in different documents. Only one listener at a
     * time. Adding a listener will remove the old one.
     */
    fun setChangeListener(listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit)

    /**
     * Firestore-specific
     *
     * Removes the current listener for changes in different notes.
     *
     * @return true if there was a listener to remove, false otherwise
     */
    fun removeChangeListener(): Boolean
}
