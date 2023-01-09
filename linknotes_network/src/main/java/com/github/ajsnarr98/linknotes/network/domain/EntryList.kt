package com.github.ajsnarr98.linknotes.network.domain

import java.io.Serializable
import java.math.BigInteger

class EntryList(private val entries: List<Entry<*>>): List<Entry<*>> by entries, Serializable {
    companion object {
        fun getEmpty(): EntryList {
            return EntryList(emptyList())
        }

        fun fromCollection(entries: Collection<Entry<*>>): EntryList {
            return EntryList(entries)
        }
    }

    private constructor(entries: Collection<Entry<*>>) : this(ArrayList(entries))

    // next entry is either 0 or one more than the most recently added entry
    private val nextEntryID: String get() {
        // default value if there are no values in list or in deleted list
        val initialId = BigInteger.ZERO
        // check if any ids exist already
        val default = initialId.minus(BigInteger.ONE) // go one less than initial to start
        val biggestActiveID: BigInteger = this.maxOfOrNull { it.id.toBigInteger() } ?: default
        return biggestActiveID.add(BigInteger.ONE).toString()
    }

    /**
     * Add the given entry, ignoring its id and giving the inserted entry an
     * appropriate id. Then sorts entries by priority.
     */
    fun withNewEntry(entry: Entry<*>): EntryList = EntryList(
        entries = (this.entries + entry.withId(id = this.nextEntryID))
            .sortedBy { e -> e.priority.value ?: Int.MAX_VALUE },
    )

    /**
     * Returns an updated list with entry with matching id removed.
     */
    fun withEntryRemovedByID(entryID: String): EntryList {
        return EntryList(this.entries.filterNot { entry -> entry.id == entryID })
    }

    /**
     * Returns a new list with the entry with a matching id updated, and all
     * entries sorted by priority.
     */
    fun withUpdated(updated: Entry<*>): EntryList {
        val index = this.indexOfFirst { entry -> entry.id == updated.id }
        return if (index >= 0) {
            EntryList(
                this.entries.toMutableList().apply {
                    this[index] = updated

                    sortBy { entry -> entry.priority.value ?: Int.MAX_VALUE }
                }
            )
        } else {
            this
        }
    }

    /**
     * Fills up any empty fields with default values. Used after saving an
     * incomplete new entry.
     */
    fun withFilledDefaults(): EntryList = EntryList(
        entries = entries.map { it.withFilledDefaults() }
    )
}
