package com.ajsnarr.peoplenotes.db

data class Tag(
    val text: String? = null,
    val color: Color? = null
) : DBObject<com.ajsnarr.peoplenotes.data.Tag> {
    companion object {
        fun fromAppObject(other: com.ajsnarr.peoplenotes.data.Tag) : Tag {
            return Tag(
                text = other.text,
                color = Color.fromAppObject(other.color)
            )
        }
    }

    override fun toAppObject(): com.ajsnarr.peoplenotes.data.Tag {
        return com.ajsnarr.peoplenotes.data.Tag(
            text = text!!,
            color = color!!.toAppObject()
        )
    }
}
