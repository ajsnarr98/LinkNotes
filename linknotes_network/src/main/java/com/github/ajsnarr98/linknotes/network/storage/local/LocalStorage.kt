package com.github.ajsnarr98.linknotes.network.storage.local

interface LocalStorage<T : java.io.Serializable> {
    /**
     * Saves to [obj] local storage. Pass in null for
     * obj to delete.
     *
     * @return true if success, false if not
     */
    suspend fun save(key: String, obj: T?): Boolean

    /**
     * Reads object in local storage based on given key.
     */
    suspend fun get(key: String): T?
}