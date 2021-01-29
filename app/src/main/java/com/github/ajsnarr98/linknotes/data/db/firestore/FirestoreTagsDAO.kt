package com.github.ajsnarr98.linknotes.data.db.firestore

class FirestoreTagsDAO : AbstractFirestoreDAO<TagTree>(TAGS_COLLECTION) {

    companion object {
        const val TAGS_COLLECTION: String = "tags"
    }

    override val tClass: Class<TagTree> = TagTree::class.java
}
