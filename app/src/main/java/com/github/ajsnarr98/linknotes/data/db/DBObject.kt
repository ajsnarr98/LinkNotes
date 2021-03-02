package com.github.ajsnarr98.linknotes.data.db

import com.github.ajsnarr98.linknotes.data.AppDataObject

/**
 * A db object only used for storage, paired with a data object used in the app.
 */
interface DBObject<AObj> {

    /**
     * Converts to the respective app data object.
     */
    fun toAppObject(): AObj
}
