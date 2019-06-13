package com.ajsnarr.peoplenotes.notes

import androidx.lifecycle.ViewModel;
import com.ajsnarr.peoplenotes.data.Entry
import com.ajsnarr.peoplenotes.data.Note

class EditNoteViewModel : ViewModel() {

    val mNote: Note = Note.newEmpty()

    val entries: MutableList<Entry>
        get() = mNote.entries
    var title: String
        get() = mNote.name
        set(value) { mNote.name = value }

    init {
        val ents = mutableListOf<Entry>(
            Entry("12345"),
            Entry("123456")
        )
        for (ent in ents) addEntry(ent)
    }

    fun addEntry(entry: Entry) {
        mNote.addEntry(entry)
    }
}
