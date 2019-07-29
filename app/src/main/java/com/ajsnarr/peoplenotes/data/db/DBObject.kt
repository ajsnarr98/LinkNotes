package com.ajsnarr.peoplenotes.data.db

/**
 * A db object only used for storage, paired with a data object used in the app.
 */
interface DBObject<DObj> {
    /**
     * Converts to the respective app data object.
     */
    fun toAppObject(): DObj
}
