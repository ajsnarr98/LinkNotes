package com.github.ajsnarr98.linknotes.data.db.firestore

import androidx.lifecycle.LifecycleOwner
import com.github.ajsnarr98.linknotes.data.*
import com.github.ajsnarr98.linknotes.data.db.DAO
import com.github.ajsnarr98.linknotes.data.db.DBInstances
import com.google.firebase.firestore.DocumentChange
import timber.log.Timber

/**
 * A notes collection that works with firestore.
 *
 * MUST clear listeners via call to onActivityEnd.
 */
class FirestoreUserCollection(private val dao: DAO<DBUser> = DBInstances.usersDAO) : UserCollection() {

    override fun generateNewUUID(newUser: User): UUID {
        return dao.upsert(DBUser.fromAppObject(newUser))
    }

    override fun add(element: User): UUID? {
        // this will work because generateNewUUID upserts in order to
        // create a new id
        return generateNewUUID(element)
    }
}
