package com.github.ajsnarr98.linknotes.network.storage.firestore

import com.github.ajsnarr98.linknotes.network.domain.Color
import com.github.ajsnarr98.linknotes.network.storage.DBObject

data class DBColor(
    val r: Int? = null,
    val g: Int? = null,
    val b: Int? = null,
    val a: Int? = null
): DBObject<Color> {
    companion object {
        fun fromAppObject(other: Color): DBColor {
            return DBColor(
                r = other.r,
                g = other.g,
                b = other.b,
                a = other.a
            )
        }
    }

    override fun toAppObject(): Color {
        return Color(
            r = this.r!!,
            g = this.g!!,
            b = this.b!!,
            a = this.a
        )
    }
}
