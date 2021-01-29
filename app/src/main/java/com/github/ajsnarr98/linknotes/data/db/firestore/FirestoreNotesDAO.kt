package com.github.ajsnarr98.linknotes.data.db.firestore

class FirestoreNotesDAO : AbstractFirestoreDAO<Note>(NOTES_COLLECTION) {

    companion object {
        const val NOTES_COLLECTION: String = "notes"
    }

    override val tClass: Class<Note> = Note::class.java
}

