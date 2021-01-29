package com.github.ajsnarr98.linknotes.data.db.firestore

import com.github.ajsnarr98.linknotes.data.TagCollection
import com.github.ajsnarr98.linknotes.data.db.DBCollectionObject

data class TagTree(
    val topValue: String? = null,
    val color: Color? = null,
    val children: MutableList<TagTree>? = null,
) : DBCollectionObject<com.github.ajsnarr98.linknotes.data.TagTree> {

    override val id: String? = topValue
    override val readableLogName: String = "Tags $topValue${TagCollection.SEPARATOR}*"

    override fun withID(id: String): DBCollectionObject<com.github.ajsnarr98.linknotes.data.TagTree> {
        return this // topValue should never be changed here
    }

    companion object {
        /**
         * Returns an entry from the database.
         */
        fun fromAppObject(other: com.github.ajsnarr98.linknotes.data.TagTree): TagTree {
            return TagTree(
                topValue = other.value,
                color = Color.fromAppObject(other.color),
                children = other.children.map { subTag -> fromAppObject(subTag) }.toMutableList(),
            )
        }
    }

    override fun toAppObject(): com.github.ajsnarr98.linknotes.data.TagTree {
        return com.github.ajsnarr98.linknotes.data.TagTree(
            value = this.topValue!!,
            color = this.color!!.toAppObject(),
            children  = this.children?.map { it.toAppObject() }?.toMutableSet() ?: mutableSetOf(),
        )
    }
}
