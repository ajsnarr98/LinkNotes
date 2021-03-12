package com.github.ajsnarr98.linknotes.data

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreUserCollection
import timber.log.Timber

object UserCollections {
    val instance = FirestoreUserCollection()
}

/**
 * Represents a collection of notes from the DB. Updates using livedata.
 *
 * DO NOT modify the value field of this class.
 */
abstract class UserCollection: MutableSet<User>, DefaultLifecycleObserver {

    var value: MutableSet<User>? = mutableSetOf<User>()

    init {
        Timber.i("Created user collection")
    }

    /**
     * Gets a new UUID to use with the user.
     *
     * Guaranteed to run iff add() is called and user is a newUser.
     */
    protected abstract fun generateNewUUID(newUser: User): UUID

    /**
     * Sets LiveData value to cause an event.
     */
    protected fun update() {
        //this.value = this.value
    }


    // inherit set methods
    override val size: Int get() = this.value?.size ?: 0
    override fun contains(element: User): Boolean = this.value?.contains(element) ?: false
    override fun containsAll(elements: Collection<User>): Boolean = this.value?.containsAll(elements) ?: false
    override fun isEmpty(): Boolean = this.value?.isEmpty() ?: true
    override fun iterator(): MutableIterator<User> = this.value?.iterator() ?: mutableSetOf<User>().iterator()

    // inherit mutable set methods
    override fun add(element: User): Boolean {

        Timber.d("Adding user to user collection: $element")

        // a new user will need a valid id
        val user = if (element.isNewUser()) element.copy(id=generateNewUUID(element)) else element

        this.value?.remove(user) // remove old if exists
        return this.value?.add(user).also { update() } ?: false
    }
    override fun addAll(elements: Collection<User>): Boolean = elements.map { user -> this.add(user) }.any { it } // add all and return true if any were added
    override fun clear() { this.value?.clear().also { update() } }
    override fun remove(element: User): Boolean = this.value?.remove(element).also { update() } ?: false
    override fun removeAll(elements: Collection<User>): Boolean = this.value?.removeAll(elements).also { update() } ?: false
    override fun retainAll(elements: Collection<User>): Boolean = this.value?.retainAll(elements).also { update() } ?: false
}
