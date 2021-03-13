package com.github.ajsnarr98.linknotes.data.db.firestore

class FirestoreUsersDAO : AbstractFirestoreDAO<DBUser>(USERS_COLLECTION) {

    companion object {
        const val USERS_COLLECTION: String = "users"
    }

    override val tClass: Class<DBUser> = DBUser::class.java
}

