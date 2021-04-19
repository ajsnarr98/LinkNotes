package com.github.ajsnarr98.linknotes.data

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize
import java.io.Serializable
import java.math.BigInteger

@Parcelize
class EntryList: ArrayList<Entry>(), Parcelable, Serializable {
    companion object {
        fun getEmpty(): EntryList {
            return EntryList()
        }

        fun fromCollection(entries: Collection<Entry>): EntryList {
            return EntryList().apply { addAll(entries) }
        }
    }

    /**
     * Note containing this list of entries.
     */
    @IgnoredOnParcel
    var parentNote: Note? = null
        set(value) {
            field = value
            this.forEach { entry -> entry.parentNote = value }
        }

    // next entry is either 0 or one more than the most recently added entry
    val nextEntryID: String get() {
        // default value if there are no values in list or in deleted list
        val initialId = BigInteger.ZERO
        // check if any ids exist already
        val default = initialId.minus(BigInteger.ONE) // go one less than initial to start
        val biggestActiveID: BigInteger = this.map { it.id.toBigInteger() }.maxOrNull() ?: default
        return biggestActiveID.add(BigInteger.ONE).toString()
    }

    /**
     * Removes the entry with the given id.
     */
    fun removeWithEntryID(entryID: String) {
        val index = this.indexOfFirst { entry -> entry.id == entryID }
        this.removeAt(index)
    }

    /**
     * Updates an existing entry in the list.
     */
    fun updateExisting(updated: Entry): Boolean {
        val index = this.indexOfFirst { entry -> entry.id == updated.id }
        if (index >= 0) {
            this[index] = updated
            this[index].parentNote = parentNote
            return true
        }
        return false
    }

}
