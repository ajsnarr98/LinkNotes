package com.ajsnarr.linknotes.data.db

data class Tag(
    val text: String? = null,
    val color: Color? = null
) : DBObject<com.ajsnarr.linknotes.data.Tag> {
    companion object {
        fun fromAppObject(other: com.ajsnarr.linknotes.data.Tag) : Tag {
            return Tag(
                text = other.text,
                color = Color.fromAppObject(other.color)
            )
        }
    }

    override fun toAppObject(): com.ajsnarr.linknotes.data.Tag {
        return com.ajsnarr.linknotes.data.Tag(
            text = text!!,
            color = color!!.toAppObject()
        )
    }
}
