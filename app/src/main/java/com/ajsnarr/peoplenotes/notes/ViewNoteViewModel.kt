package com.ajsnarr.peoplenotes.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ajsnarr.peoplenotes.data.Note

class ViewNoteViewModel(note: Note) {

    /**
     * Unlike EditNoteViewModel, passed in note cannot be null.
     */
    class Factory(val note: Note) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ViewNoteViewModel::class.java)) {
                EditNoteViewModel(note) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}