package com.github.ajsnarr98.linknotes.data.db

/**
 * A group of default instances of DAO objects.
 */
object DBInstances {
    val notesDAO: NotesDAO = FirestoreNotesDAO()
    val tagsDAO: TagsDAO = FirestoreTagsDAO()
}