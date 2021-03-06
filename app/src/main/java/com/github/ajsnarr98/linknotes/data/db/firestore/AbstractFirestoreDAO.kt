package com.github.ajsnarr98.linknotes.data.db.firestore

import com.github.ajsnarr98.linknotes.data.AppDataObject
import com.github.ajsnarr98.linknotes.data.db.DAO
import com.github.ajsnarr98.linknotes.data.db.DBCollectionObject
import com.google.firebase.firestore.*
import timber.log.Timber
import java.lang.Exception

/**
 * Abstract Firestore DAO that uses given collectionName and a userId to do operations.
 *
 * @property collectionName Firestore collection name.
 */
abstract class AbstractFirestoreDAO<T : DBCollectionObject<out AppDataObject>>(
    private val collectionName: String,
    private val userId: String,
) : DAO<T>, FirestoreChangeListenerHolder {

    companion object {
        private const val USER_COLLECTION_NAME = "users"
    }

    init {
        Timber.i("Making connection to DB for user '$userId'")
    }

    private val db = FirebaseFirestore.getInstance()
    private val user = db.collection(USER_COLLECTION_NAME).document(userId)
    private var listenerRegistration: ListenerRegistration? = null

    /**
     * Returns the T::class.java stored in this collection.
     */
    protected abstract val tClass: Class<T>

    override fun setChangeListener(listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit) {
        removeChangeListener() // remove if exists
        listenerRegistration = user.collection(collectionName).addSnapshotListener(listener)
    }

    override fun removeChangeListener(): Boolean {
        listenerRegistration?.remove()
        val removed = listenerRegistration != null
        if (removed) { listenerRegistration = null }

        return removed
    }

    override fun getAll(
        onSuccess: (T) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        user.collection(collectionName)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    onSuccess(document.toObject(tClass))
                }
            }
            .addOnFailureListener(onFailure)
    }

    override fun upsert(document: T): String {
        Timber.d("upserting to $collectionName...")

        // generate a new document if neccesary
        val documentRef: DocumentReference
                = if (document.hasID()) {
            user.collection(collectionName)
                .document(document.id!!)
        } else {
            // new document without id; generate this doc an id
            user.collection(collectionName)
                .document()
        }

        val savedDocument
                = if (document.hasID()) {
            document
        } else {
            // add new id to document
            document.withID(documentRef.id)
        }

        documentRef.set(savedDocument)
            .addOnSuccessListener {
                Timber.d("Successfully upserted document to $collectionName")
            }
            .addOnFailureListener { err -> Timber.e("Failed to upsert document to $collectionName: $err") }


        return savedDocument.id ?: throw IllegalStateException("Document id should not be null (${document.readableLogName})")
    }

    override fun delete(document: T) {
        Timber.d("deleting document in $collectionName...")

        if (document.hasID()) {
            user.collection(collectionName)
                .document(document.id!!)
                .delete()
                .addOnSuccessListener { Timber.i("$collectionName document ${document.readableLogName} successfully deleted") }
                .addOnFailureListener { Timber.e("$collectionName document ${document.readableLogName} failed to delete") }
        } else {
            Timber.e("Cannot delete document with empty ID")
        }
    }

    override fun deleteAll(documents: Collection<T>) {
        for (doc in documents) {
            delete(doc)
        }
    }
}
