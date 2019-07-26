package com.ajsnarr.peoplenotes.data


data class Tag(val text: String, val color: Color = Color.randomTagColor())
    : DataObject<com.ajsnarr.peoplenotes.db.Tag> {
    companion object {
        fun fromDBTag(other: com.ajsnarr.peoplenotes.db.Tag): Tag {
            return Tag(
                text = other.text!!,
                color = Color.fromDBColor(other.color!!)
            )
        }
    }

    override fun toDBObject(): com.ajsnarr.peoplenotes.db.Tag {
        return com.ajsnarr.peoplenotes.db.Tag(
            text = text,
            color = color.toDBObject()
        )
    }
}