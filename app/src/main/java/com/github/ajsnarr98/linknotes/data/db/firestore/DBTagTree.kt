package com.github.ajsnarr98.linknotes.data.db.firestore

import com.github.ajsnarr98.linknotes.data.TagCollection
import com.github.ajsnarr98.linknotes.data.db.DBCollectionObject

data class DBTagTree(
    val topValue: String? = null,
    val color: DBColor? = null,
    val children: MutableList<DBTagTree>? = null,
) : DBCollectionObject<com.github.ajsnarr98.linknotes.data.TagTree> {

    override val id: String? = topValue
    override val readableLogName: String = "Tags $topValue${TagCollection.SEPARATOR}*"

    override fun equals(other: Any?): Boolean {
        return this === other || other is DBTagTree
                && this.topValue == other.topValue
    }

    override fun hashCode(): Int {
        return topValue.hashCode()
    }

    override fun withID(id: String): DBCollectionObject<com.github.ajsnarr98.linknotes.data.TagTree> {
        return this // topValue should never be changed here
    }

    companion object {
        /**
         * Returns an entry from the database.
         */
        fun fromAppObject(other: com.github.ajsnarr98.linknotes.data.TagTree): DBTagTree {
            return DBTagTree(
                topValue = other.value,
                color = DBColor.fromAppObject(other.color),
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
