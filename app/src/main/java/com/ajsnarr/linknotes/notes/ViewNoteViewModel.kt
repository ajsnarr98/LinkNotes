package com.ajsnarr.linknotes.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ajsnarr.linknotes.data.Note

class ViewNoteViewModel(val note: Note): ViewModel() {

    /**
     * Unlike EditNoteViewModel, passed in note cannot be null.
     */
    class Factory(val note: Note) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(ViewNoteViewModel::class.java)) {
                ViewNoteViewModel(note) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}