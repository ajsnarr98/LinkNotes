package com.github.ajsnarr98.linknotes.data.db

data class Tag(
    val text: String? = null,
    val color: Color? = null
) : DBObject<com.github.ajsnarr98.linknotes.data.Tag> {
    companion object {
        fun fromAppObject(other: com.github.ajsnarr98.linknotes.data.Tag) : Tag {
            return Tag(
                text = other.text,
                color = Color.fromAppObject(other.color)
            )
        }
    }

    override fun toAppObject(): com.github.ajsnarr98.linknotes.data.Tag {
        return com.github.ajsnarr98.linknotes.data.Tag(
            text = text!!,
            color = color!!.toAppObject()
        )
    }
}
