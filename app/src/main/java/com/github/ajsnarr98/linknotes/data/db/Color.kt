package com.github.ajsnarr98.linknotes.data.db

data class Color(
    val r: Int? = null,
    val g: Int? = null,
    val b: Int? = null,
    val a: Int? = null
): DBObject<com.github.ajsnarr98.linknotes.data.Color> {
    companion object {
        fun fromAppObject(other: com.github.ajsnarr98.linknotes.data.Color): Color {
            return Color(
                r = other.r,
                g = other.g,
                b = other.b,
                a = other.a
            )
        }
    }

    override fun toAppObject(): com.github.ajsnarr98.linknotes.data.Color {
        return com.github.ajsnarr98.linknotes.data.Color(
            r = this.r!!,
            g = this.g!!,
            b = this.b!!,
            a = this.a
        )
    }
}
