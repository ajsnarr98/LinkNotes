package com.github.ajsnarr98.linknotes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import kotlin.reflect.KClass
import kotlin.reflect.full.createInstance

sealed class EntryType(open var value: String) : AppDataObject, Parcelable, Serializable {

    @Parcelize class DEFAULT : EntryType("")
    @Parcelize class IMAGES : EntryType("images")
    @Parcelize class CUSTOM(override var value: String) : EntryType(value)

    companion object {
        /**
         * Map of string values to functions that construct the respective
         * class.
         */
        private val valueMap = mutableMapOf<String, () -> EntryType>().apply {
           for (type in EntryType::class.sealedSubclasses) {
               if (type != CUSTOM::class) {
                   val constructor = getConstructor0(type)
                   this[constructor().value] = constructor
               }
           }
        }

        private fun <T : EntryType> getConstructor0(clazz: KClass<T>): () -> T {
            return { clazz.createInstance() }
        }

        fun forValue(value: String): EntryType = valueMap[value]?.invoke() ?: CUSTOM(value)
    }
}
