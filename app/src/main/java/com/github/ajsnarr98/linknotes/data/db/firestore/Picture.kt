package com.github.ajsnarr98.linknotes.data.db.firestore

import com.github.ajsnarr98.linknotes.data.db.DBObject

data class Picture(val url: String? = null): DBObject<String> {

    companion object {
        fun fromAppObject(other: String) : Picture {
            return Picture(url = other)
        }
    }

    override fun toAppObject(): String {
        return this.url!!
    }
}
