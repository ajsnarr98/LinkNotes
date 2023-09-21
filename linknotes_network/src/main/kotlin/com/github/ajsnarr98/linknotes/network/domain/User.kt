package com.github.ajsnarr98.linknotes.network.domain

import com.github.ajsnarr98.linknotes.data.UUID

/**
 * User with an id.
 *
 * @property id id
 * @property googleID google account ID used during sign in
 * @property googleIDToken current auth token used for the google account
 */
data class User(
    val id: UUID?, // an id will be assigned when stored in db
    val googleID: String? = null,
    val googleIDToken: String? = null,
) : AppDataObject, java.io.Serializable {

    /**
     * A user is marked as a new user when it has no valid ID.
     *
     * An invalid ID will either be null or blank (at most whitespace chars).
     */
    fun isNewUser(): Boolean {
        return this.id == null || this.id.isBlank()
    }
}