package com.ajsnarr.peoplenotes.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EntryType(var type: String): AppDataObject, Parcelable {
    companion object {
        val DEFAULT get() = EntryType("")
        val IMAGES get() = EntryType("images")
    }
}
