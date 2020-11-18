package com.ajsnarr.linknotes.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tag(val text: String, val color: Color = Color.randomTagColor())
    : AppDataObject, Parcelable {
}
