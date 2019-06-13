package com.ajsnarr.peoplenotes.data

data class EntryType(var type: String) {
    companion object {
        val EMPTY = EntryType("")
    }
}