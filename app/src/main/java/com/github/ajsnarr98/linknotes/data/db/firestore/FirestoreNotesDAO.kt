package com.github.ajsnarr98.linknotes.data.db.firestore

class FirestoreNotesDAO : AbstractFirestoreDAO<DBNote>(NOTES_COLLECTION) {

    companion object {
        const val NOTES_COLLECTION: String = "notes"
    }

    override val tClass: Class<DBNote> = DBNote::class.java
}

