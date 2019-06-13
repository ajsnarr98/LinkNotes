package com.ajsnarr.peoplenotes.data

data class EntryContent(var content: String) {
    companion object {
        val EMPTY = EntryContent("")
    }
}