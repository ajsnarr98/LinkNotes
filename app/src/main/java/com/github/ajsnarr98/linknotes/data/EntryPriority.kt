package com.github.ajsnarr98.linknotes.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

/**
 * Determines the priority of this entry on the list.
 *
 * The lower the _number_, the higher the _priority_ towards the beginning
 * of the list. Null is always the lowest priority.
 */
@Parcelize
data class EntryPriority(
    val value: Int?
): Parcelable, AppDataObject, Serializable {
    companion object {
        val LOWEST: EntryPriority = EntryPriority(null)
        val PINNED: EntryPriority = EntryPriority(Int.MIN_VALUE)
    }
}