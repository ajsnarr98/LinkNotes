package com.github.ajsnarr98.linknotes.network.domain

import java.io.Serializable

sealed class EntryType<T : Entry<T>>(open var value: String) : AppDataObject, Serializable {

    object Default : EntryType<Entry.Default>("")
    object Images : EntryType<Entry.Images>("images")
    class Custom(override var value: String) : EntryType<Entry.Custom>(value)

    companion object {
        /**
         * Map of string values to functions that construct the respective
         * class.
         */
        private val valueMap = mutableMapOf<String, EntryType<*>>().apply {
           for (type in EntryType::class.sealedSubclasses) {
               val objInstance = type.objectInstance
               if (objInstance != null) {
                   this[objInstance.value] = objInstance
               }
           }
        }

        fun forValue(value: String): EntryType<*> = valueMap[value] ?: Custom(value)
    }
}
