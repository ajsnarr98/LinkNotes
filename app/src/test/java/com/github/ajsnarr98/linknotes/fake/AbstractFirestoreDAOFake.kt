package com.github.ajsnarr98.linknotes.fake

import com.github.ajsnarr98.linknotes.data.AppDataObject
import com.github.ajsnarr98.linknotes.data.db.DAO
import com.github.ajsnarr98.linknotes.data.db.DBCollectionObject
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreChangeListenerHolder
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception

/**
 * B and T should be the same.
 */
abstract class AbstractFirestoreDAOFake<T : DBCollectionObject<out AppDataObject>> : DAO<T>, FirestoreChangeListenerHolder {

    /**
     * Collection of things in the "database".
     */
    val collection = mutableSetOf<T>()

    override fun getAll(onSuccess: (T) -> Unit, onFailure: (Exception) -> Unit) {
        for (doc in collection) {
            onSuccess(doc)
        }
    }

    override fun upsert(document: T): String {
        this.collection.remove(document)
        this.collection.add(document)
        return document.id ?: "0" // some UUID
    }

    override fun delete(document: T) {
        this.collection.remove(document)
    }

    override fun deleteAll(documents: Collection<T>) {
        this.collection.removeAll(documents)
    }

    override fun setChangeListener(listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit) {
        // no op
    }

    override fun removeChangeListener(): Boolean {
        // no op
        return true
    }
}