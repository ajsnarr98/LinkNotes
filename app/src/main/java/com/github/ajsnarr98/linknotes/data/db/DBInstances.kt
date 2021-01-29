package com.github.ajsnarr98.linknotes.data.db

import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreNotesDAO
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreTagsDAO

/**
 * A group of default instances of DAO objects.
 */
object DBInstances {
    val notesDAO: NotesDAO = FirestoreNotesDAO()
    val tagsDAO: TagsDAO = FirestoreTagsDAO()
}