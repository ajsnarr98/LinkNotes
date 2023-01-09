package com.github.ajsnarr98.linknotes.network.storage.firestore

import com.github.ajsnarr98.linknotes.network.storage.DBObject

data class DBPicture(val url: String? = null): DBObject<String> {

    companion object {
        fun fromAppObject(other: String) : DBPicture {
            return DBPicture(url = other)
        }
    }

    override fun toAppObject(): String {
        return this.url!!
    }
}
