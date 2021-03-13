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

    init {
        // get notes from db
        dao.getAll(
            onSuccess = {user: DBUser -> this.safeAdd(user); Timber.v("Received user ${user.id} from database")},
            onFailure = {err  -> Timber.e("Error getting user from db: $err")}
        )
    }

    /**
     * Add a listener for updating notes based on remote changes.
     */
    override fun onStart(owner: LifecycleOwner) {
        if (dao is FirestoreChangeListenerHolder) {
            dao.setChangeListener { snapshots, firebaseFirestoreException ->
                if (snapshots?.documentChanges == null) return@setChangeListener

                Timber.i("Remote changes received in note collection")

                for (dc in snapshots.documentChanges) {
                    val user = dc.document.toObject(DBUser::class.java)
                    when (dc.type) {
                        DocumentChange.Type.ADDED -> this.safeAdd(user)
                        DocumentChange.Type.REMOVED -> this.safeRemove(user)
                        DocumentChange.Type.MODIFIED -> {
                            if (this.contains(user.toAppObject())) {
                                this.safeRemove(user)
                                this.safeAdd(user)
                            }
                        }
                        else -> return@setChangeListener
                    }
                }
                // update based on current changes
                update()
            }
        } else {
            Timber.e("Unexpected behavior encountered. DAO should be a FirestoreChangeListenerHolder")
        }
    }

    /**
     * Remove listener at end of activity.
     */
    override fun onStop(owner: LifecycleOwner) {
        if (dao is FirestoreChangeListenerHolder) {
            dao.removeChangeListener()
        } else {
            Timber.e("Unexpected behavior encountered. DAO should be a FirestoreChangeListenerHolder")
        }
    }


    override fun generateNewUUID(newUser: User): UUID {
        // this method runs right before add is called on this Note, so
        // upsert note here
        return dao.upsert(DBUser.fromAppObject(newUser))
    }

    /**
     * Adds a note to set without updating the database.
     */
    private fun safeAdd(element: DBUser) {
        super.add(element.toAppObject())
    }

    /**
     * Removes a note from set without updating the database.
     */
    private fun safeRemove(element: DBUser) {
        super.remove(element.toAppObject())
    }

    // inherit mutable set methods (and configure to update db)

    override fun add(element: User): Boolean {
        return super.add(element).also {
            // knowing generating a newUUID will upsert, only upsert if element
            // is not a new note
            if (element.isNewUser() == false) dao.upsert(
                DBUser.fromAppObject(
                    element
                )
            )
        }
    }
    override fun clear() {
        val toDelete = this.map { user -> DBUser.fromAppObject(user) }
        super.clear()
        dao.deleteAll(toDelete)
    }
    override fun remove(element: User): Boolean {
        return super.remove(element).also {
            dao.delete(DBUser.fromAppObject(element))
        }
    }
    override fun removeAll(elements: Collection<User>): Boolean {
        return super.removeAll(elements).also {
            dao.deleteAll(elements.map { user -> DBUser.fromAppObject(user) })
        }
    }
    override fun retainAll(elements: Collection<User>): Boolean {
        // give dao the inverse of elements being kept in the set, to remove
        val removeSet = this.filter { user -> !elements.contains(user) }
        return super.retainAll(elements).also {
            dao.deleteAll(removeSet.map { user -> DBUser.fromAppObject(user) })
        }
    }
}
