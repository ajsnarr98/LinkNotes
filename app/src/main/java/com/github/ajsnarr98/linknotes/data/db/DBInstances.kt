package com.github.ajsnarr98.linknotes.data.db

import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreNotesDAO
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreTagsDAO
import com.github.ajsnarr98.linknotes.data.db.firestore.DBNote
import com.github.ajsnarr98.linknotes.data.db.firestore.DBTagTree


/**
 * A group of default instances of DAO objects.
 */
object DBInstances {
    val notesDAO: DAO<DBNote> = FirestoreNotesDAO()
    val tagsDAO: DAO<DBTagTree> = FirestoreTagsDAO()
}