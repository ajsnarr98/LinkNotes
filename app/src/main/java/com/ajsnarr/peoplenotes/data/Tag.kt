package com.ajsnarr.peoplenotes.data


data class Tag(val text: String, val color: Color = Color.randomTagColor()) {
    companion object {
        fun fromDBTag(other: com.ajsnarr.peoplenotes.db.Tag): Tag {
            return Tag(
                text = other.text!!,
                color = Color.fromDBColor(other.color!!)
            )
        }
    }
}