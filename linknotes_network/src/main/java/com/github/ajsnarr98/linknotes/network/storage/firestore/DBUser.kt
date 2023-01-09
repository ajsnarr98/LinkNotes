package com.github.ajsnarr98.linknotes.network.storage.firestore

import com.github.ajsnarr98.linknotes.network.domain.User
import com.github.ajsnarr98.linknotes.network.storage.DBCollectionObject

data class DBUser(
    override val id: String? = null, // a null id will be assigned when stored in db
    val googleID: String? = null,
) : DBCollectionObject<User> {

    companion object {
        fun fromAppObject(other: User) : DBUser {
            return DBUser(
                id = other.id,
                googleID = other.googleID,
            )
        }
    }

    override fun withID(id: String): DBCollectionObject<User> {
        return this.copy(id = id)
    }

    override fun toAppObject(): User {
        return User(
            id = id,
            googleID = googleID,
        )
    }
}