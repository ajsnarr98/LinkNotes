package com.ajsnarr.peoplenotes.data


data class Tag(val text: String, val color: Color = Color.randomTagColor())
    : AppDataObject
