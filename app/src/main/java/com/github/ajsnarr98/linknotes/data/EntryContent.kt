package com.github.ajsnarr98.linknotes.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EntryContent(var value: String): AppDataObject, Parcelable {
    companion object {
        val EMPTY get() = EntryContent("")
    }
}