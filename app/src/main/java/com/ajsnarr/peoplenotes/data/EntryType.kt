package com.ajsnarr.peoplenotes.data

data class EntryType(var type: String): AppDataObject {
    companion object {
        val EMPTY get() = EntryType("none")
    }
}