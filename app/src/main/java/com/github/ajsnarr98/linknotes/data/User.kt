package com.github.ajsnarr98.linknotes.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class User(
    val id: UUID?, // a null id will be assigned when stored in db
    val googleID: String? = null,
) : AppDataObject, Parcelable {
    /**
     * A user is marked as a new user when it has no valid ID.
     *
     * An invalid ID will either be null or blank (at most whitespace chars).
     */
    fun isNewUser(): Boolean {
        return this.id == null || this.id.isBlank()
    }
}