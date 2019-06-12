package com.ajsnarr.peoplenotes.notes

import androidx.lifecycle.ViewModel;
import com.ajsnarr.peoplenotes.data.Entry

class EditNoteViewModel : ViewModel() {

    val entries = mutableListOf<Entry>(
        Entry("12345"),
        Entry("123456")
    )

    init {
    }

    fun addEntry(entry: Entry) {
        entries.add(entry)
    }
}
