package com.ajsnarr.peoplenotes.data

import java.lang.IllegalArgumentException
import kotlin.random.Random

data class Color(val r: Int, val g: Int, val b: Int, val a: Int? = null)
    : AppDataObject {

    companion object {

        /**
         * @param rgbHex Must be in the format "#rrggbb"
         */
        fun withRGBHex(rgbHex: String): Color {
            if (rgbHex.length != 7) throw IllegalArgumentException()

            TODO()
        }

        /**
         * Get random bright color
         */
        fun randomTagColor(): Color {
            // generate random value in hsl scale to get brightest colors
            val hue = Random.nextInt(0, 360)
            val saturation = 0.65
            val lightness = 0.65

            return fromHSL(hue, saturation, lightness)
        }

        /**
         * Converts from hsl to rgb color.
         *
         * @param h 0 <= h <= 360
         * @param s 0 <= s <= 1
         * @param l 0 <= l <= 1
         */
        fun fromHSL(h: Int, s: Double, l: Double): Color {

            if (h < 0 || h > 360) {
                throw IllegalArgumentException("h must be in range 0 <= h <= 360")
            } else if (s < 0 || s > 1) {
                throw IllegalArgumentException("s must be in range 0 <= s <= 1")
            } else if (l < 0 || l > 1) {
                throw IllegalArgumentException("l must be in range 0 <= l <= 1")
            }

            val c = (1 - Math.abs(2 * l - 1)) * s
            val x = c * (1 - Math.abs((h / 60) % 2 - 1))
            val m = l - c / 2

            val rgbPrime: DoubleArray = when (h) {
                in 0..59    -> doubleArrayOf(c, x, 0.0)
                in 60..119  -> doubleArrayOf(x, c, 0.0)
                in 120..179 -> doubleArrayOf(0.0, c, x)
                in 180..239 -> doubleArrayOf(0.0, x, c)
                in 240..299 -> doubleArrayOf(x, 0.0, c)
                in 300..360 -> doubleArrayOf(c, 0.0, x)
                else        -> throw IllegalArgumentException("h must be in range 0 <= h <= 360")
            }

            val rgb = IntArray(3)
            for (i in 0..2) { rgb[i] = ((rgbPrime[i] + m) * 255).toInt() }

            return Color(rgb[0], rgb[1], rgb[2])
        }
    }
}