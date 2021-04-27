package com.github.ajsnarr98.linknotes.notes

import android.view.View
import com.github.ajsnarr98.linknotes.data.Entry

/**
 * A special view that holds some kind of entry.
 *
 * Anything that inherits from this interface must be a View.
 */
interface EntryView {
    val view: View
        get() = if (this is View)
            this
        else
            throw IllegalStateException("EntryView interface should only be implemented by Views")

    fun bind(entry: Entry)
}