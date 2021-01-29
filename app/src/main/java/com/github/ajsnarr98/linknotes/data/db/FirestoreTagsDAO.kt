package com.github.ajsnarr98.linknotes.data.db

import java.lang.IllegalStateException

class FirestoreTagsDAO : AbstractFirestoreDAO<com.github.ajsnarr98.linknotes.data.TagTree>(TAGS_COLLECTION), TagsDAO {

    companion object {
        const val TAGS_COLLECTION: String = "tags"
    }

    override fun <T : DBCollectionObject<com.github.ajsnarr98.linknotes.data.TagTree>> getConversionClass(): Class<T> {
        // we need a DBCollectionObject<TagTree>
        return TagTree::class.java as? Class<T> ?: throw IllegalStateException("This should not happen")
    }
}
