package com.github.ajsnarr98.linknotes.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EntryType(var value: String): AppDataObject, Parcelable {
    companion object {
        val DEFAULT get() = EntryType("")
        val IMAGES get() = EntryType("images")
    }
}
