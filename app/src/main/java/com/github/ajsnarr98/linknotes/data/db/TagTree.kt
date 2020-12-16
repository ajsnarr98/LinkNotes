package com.github.ajsnarr98.linknotes.data.db

data class TagTree(
    val topValue: String? = null,
    val color: Color? = null,
    val children: MutableList<TagTree>? = null,
) : DBObject<com.github.ajsnarr98.linknotes.data.TagTree> {
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