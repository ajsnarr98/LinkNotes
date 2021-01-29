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
@Suppress("UNCHECKED_CAST")
abstract class AbstractFirestoreDAOFake<A : AppDataObject, B : DBCollectionObject<A>> : DAO<A>, FirestoreChangeListenerHolder {

    /**
     * Collection of things in the "database".
     */
    val collection = mutableSetOf<B>()

    override fun <T : DBCollectionObject<A>> getAll(
        onSuccess: (T) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        for (doc in collection) {
            onSuccess(doc as T)
        }
    }

    override fun <T : DBCollectionObject<A>> upsert(document: T): String {
        this.collection.add(document as B)
        return document.id ?: "0" // some UUID
    }

    override fun <T : DBCollectionObject<A>> delete(document: T) {
        this.collection.remove(document as B)
    }

    override fun <T : DBCollectionObject<A>> deleteAll(documents: Collection<T>) {
        this.collection.removeAll(documents.map { it as B })
    }

    override fun setChangeListener(listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit) {
        // no op
    }

    override fun removeChangeListener(): Boolean {
        // no op
        return true
    }
}