package com.github.ajsnarr98.linknotes.data

import timber.log.Timber

/**
 * Represents a collection of notes from the DB. Updates using livedata.
 *
 * DO NOT modify the value field of this class.
 */
abstract class UserCollection {

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
     * Adds a new/existing user to the set of users. Returns the user's ID
     * if successful, null if not.
     */
    abstract fun add(element: User): UUID?
}
