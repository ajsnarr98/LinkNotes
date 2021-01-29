package com.github.ajsnarr98.linknotes.data.db

import com.github.ajsnarr98.linknotes.data.AppDataObject
import java.lang.Exception

/**
 * A general database access object for pulling from/writing to database.
 */
interface DAO<A : AppDataObject> {
    /**
     * Gets all documents this dao has access to.
     */
    fun <T : DBCollectionObject<A>> getAll(onSuccess: (T) -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Updates an existing document or inserts this one if it does not exist.
     *
     * Returns the document's UUID.
     */
    fun <T : DBCollectionObject<A>> upsert(document: T): String

    /**
     * Deletes a document.
     */
    fun <T : DBCollectionObject<A>> delete(document: T)

    /**
     * Deletes all given documents.
     */
    fun <T : DBCollectionObject<A>> deleteAll(documents: Collection<T>)
}