package com.ajsnarr.peoplenotes.data

/**
 * A data object used in the app, paired with a database object only used for storage.
 */
interface DataObject<DBObject> {
    /**
     * Converts to the respective database object.
     */
    fun toDBObject(): DBObject
}