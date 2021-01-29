package com.github.ajsnarr98.linknotes.data.db

import com.github.ajsnarr98.linknotes.data.AppDataObject
import java.lang.Exception

/**
 * A general database access object for pulling from/writing to database.
 */
interface DAO<T : DBCollectionObject<out AppDataObject>> {
    /**
     * Gets all documents this dao has access to.
     */
    fun getAll(onSuccess: (T) -> Unit, onFailure: (Exception) -> Unit)

    /**
     * Updates an existing document or inserts this one if it does not exist.
     *
     * Returns the document's UUID.
     */
    fun upsert(document: T): String

    /**
     * Deletes a document.
     */
    fun delete(document: T)

    /**
     * Deletes all given documents.
     */
    fun deleteAll(documents: Collection<T>)
}