package com.github.ajsnarr98.linknotes.data.db.firestore

class FirestoreTagsDAO(userId: String) : AbstractFirestoreDAO<DBTagTree>(TAGS_COLLECTION, userId) {

    companion object {
        const val TAGS_COLLECTION: String = "tags"
    }

    override val tClass: Class<DBTagTree> = DBTagTree::class.java
}
