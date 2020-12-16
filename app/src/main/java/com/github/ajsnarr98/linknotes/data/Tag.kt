package com.github.ajsnarr98.linknotes.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tag(val text: String, val color: Color = Color.randomTagColor())
    : AppDataObject, Parcelable {

}
