package com.ajsnarr.peoplenotes.data

import android.os.Parcelable
import com.ajsnarr.peoplenotes.util.max
import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import java.math.BigInteger

@Parcelize
class EntryList: ArrayList<Entry>(), Parcelable {
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

    @IgnoredOnParcel
    private val lastDeletedEntries = mutableSetOf<Entry>()

    // next entry is either 0 or one more than the most recently added entry
    val nextEntryID: String get() {
        return if (this.isEmpty()) {
            "0"
        } else {
            // check if a deleted ID was the previous largest index
            val biggestActiveID: BigInteger = this.last().id.toBigInteger()
            val biggestDeletedID: BigInteger = lastDeletedEntries.map { it.id.toBigInteger() }.max() ?: Int.MIN_VALUE.toBigInteger()
            max(biggestActiveID, biggestDeletedID).toString()
        }
    }

    /**
     * Clears all deleted entries from this list. Can no longer restore afterwards.
     */
    fun clearDeletedEntries() {
        lastDeletedEntries.clear()
    }

    override fun remove(element: Entry): Boolean {
        return super.remove(element).also { lastDeletedEntries.add(element) }
    }

    override fun removeAt(index: Int): Entry {
        val entry = super.removeAt(index)
        lastDeletedEntries.add(entry)
        return entry
    }

    /**
     * Removes the entry with the given id.
     */
    fun removeWithEntryID(entryID: String) {
        val index = this.indexOfFirst { entry -> entry.id == entryID }
        this.removeAt(index)
    }

    /**
     * Restores all entries removed since the last call to
     * clearDeletedEntries().
     */
    fun restoreRecentlyDeleted() {
        for (entry in lastDeletedEntries) {
            val ind = getAppropriateIndex(entry)
            if (ind >= 0) {
                this.add(ind, entry)
            }
        }
        clearDeletedEntries()
    }

    /**
     * Used for re-adding deleted entries. Finds a suitable index in the list
     * based on ID.
     *
     * Returns -1 if there is an unexpected error.
     */
    private fun getAppropriateIndex(entry: Entry): Int {
        // use a binary search
        if (this.size == 0) return 0
        var ind = this.size / 2
        var upper = this.size - 1
        var lower = 0
        while (upper != lower) {
            if (this[ind].id < entry.id) {
                lower = ind
            } else if (this[ind].id > entry.id ) {
                upper = ind
            } else {
                return -1 // stop because there should not be an entry with matching ID still in the list
            }
        }
        // ind == upper == lower
        while (ind != (this.size + 1) && this[ind].id < entry.id) {
            ind++ // id at index should be next greatest
        }
        return ind
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
