package com.ajsnarr.peoplenotes.data

data class EntryContent(var content: String): AppDataObject {
    companion object {
        val EMPTY get() = EntryContent("")
    }
}
