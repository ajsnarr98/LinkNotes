package com.ajsnarr.peoplenotes.db

import com.ajsnarr.peoplenotes.data.AppDataObject

/**
 * A db object only used for storage, paired with a data object used in the app.
 */
interface DBObject<DObj> {
    /**
     * Converts to the respective app data object.
     */
    fun toAppObject(): DObj
}
