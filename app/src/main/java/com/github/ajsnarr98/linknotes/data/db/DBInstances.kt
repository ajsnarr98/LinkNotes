package com.github.ajsnarr98.linknotes.data.db

import com.github.ajsnarr98.linknotes.data.db.firestore.*


/**
 * A group of default instances of DAO objects.
 */
object DBInstances {
    val notesDAO: DAO<DBNote> = FirestoreNotesDAO()
    val tagsDAO: DAO<DBTagTree> = FirestoreTagsDAO()
    val usersDAO: DAO<DBUser> = FirestoreUsersDAO()
}