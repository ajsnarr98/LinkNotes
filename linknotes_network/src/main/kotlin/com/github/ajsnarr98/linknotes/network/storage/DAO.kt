package com.github.ajsnarr98.linknotes.network.storage

import com.github.ajsnarr98.linknotes.network.domain.AppDataObject

/**
 * A general database access object for pulling from/writing to database.
 */
interface DAO<T : DBCollectionObject<out AppDataObject>> {
    /**
     * Gets all documents this dao has access to.
     */
    suspend fun getAll(): Collection<T>

    /**
     * Updates an existing document or inserts this one if it does not exist.
     *
     * If this document has an invalid id, assigns one.
     *
     * Returns the document's UUID, or throws an error.
     */
    suspend fun upsert(document: T): String

    /**
     * Deletes a document.
     */
    suspend fun delete(document: T): T

    /**
     * Deletes all given documents.
     */
    suspend fun deleteAll(documents: Collection<T>)
}