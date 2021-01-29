package com.github.ajsnarr98.linknotes.data.db

import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreNotesDAO
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreTagsDAO
import com.github.ajsnarr98.linknotes.data.db.firestore.Note
import com.github.ajsnarr98.linknotes.data.db.firestore.TagTree


/**
 * A group of default instances of DAO objects.
 */
object DBInstances {
    val notesDAO: DAO<Note> = FirestoreNotesDAO()
    val tagsDAO: DAO<TagTree> = FirestoreTagsDAO()
}