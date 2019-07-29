package com.ajsnarr.peoplenotes.db

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