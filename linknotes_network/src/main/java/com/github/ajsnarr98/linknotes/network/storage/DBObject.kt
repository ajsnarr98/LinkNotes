package com.github.ajsnarr98.linknotes.network.storage

/**
 * A db object only used for storage, paired with a data object used in the app.
 */
interface DBObject<AObj> {

    /**
     * Converts to the respective app data object.
     */
    fun toAppObject(): AObj
}
