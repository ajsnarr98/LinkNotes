package com.github.ajsnarr98.linknotes.network.storage

/**
 * A DB object only used for storage. Differs from [DBObject] in that it must
 * have a unique ID, and is meant for direct storage in the DB.
 */
interface DBCollectionObject<AObj> : DBObject<AObj> {

    /**
     * The unique ID of this document.
     */
    val id: String?

    /**
     * Optional human readable way to refer to this object (used in logs).
     */
    val readableLogName: String get() = id ?: ""

    /**
     * Returns a copy of this object with the given id.
     */
    fun withID(id: String) : DBCollectionObject<AObj>

    /**
     * Whether or not this object has an ID yet. Some objects may not get an ID
     * until first inserted in the DB.
     */
    fun hasID(): Boolean {
        return this.id?.isNotBlank() ?: false // return false if id is null
    }
}