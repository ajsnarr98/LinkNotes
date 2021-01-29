package com.github.ajsnarr98.linknotes.data.db.firestore

import com.github.ajsnarr98.linknotes.data.db.DBCollectionObject
import com.github.ajsnarr98.linknotes.data.db.NotesDAO
import java.lang.IllegalStateException

class FirestoreNotesDAO : AbstractFirestoreDAO<com.github.ajsnarr98.linknotes.data.Note>(
    NOTES_COLLECTION
), NotesDAO {

    companion object {
        const val NOTES_COLLECTION: String = "notes"
    }

    override fun <T : DBCollectionObject<com.github.ajsnarr98.linknotes.data.Note>> getConversionClass(): Class<T> {
        // we need a DBCollectionObject<Note>
        return Note::class.java as? Class<T> ?: throw IllegalStateException("This should not happen")
    }
}

