package com.github.ajsnarr98.linknotes

import com.github.ajsnarr98.linknotes.data.NoteCollection
import com.github.ajsnarr98.linknotes.data.TagCollection
import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.data.UserCollection
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreNoteCollection
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreTagCollection
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreUserCollection
import com.github.ajsnarr98.linknotes.data.local.AccountStore

/**
 * An object that provides default implementations (global scope) during runtime.
 */
object Providers {
    val accountStore: AccountStore?
        get() = accountStoreProvider.instance

    val userCollection: UserCollection?
        get() = userCollectionProvider.instance

    val noteCollection: NoteCollection?
        get() = noteCollectionProvider.instance

    val tagCollection: TagCollection?
        get() = tagCollectionProvider.instance

    // default provider implementations
    var accountStoreProvider: Provider<AccountStore?>
            = BasicProvider(null)
    var userCollectionProvider: Provider<UserCollection?>
            = LazyProvider { FirestoreUserCollection() }
    var noteCollectionProvider: Provider<NoteCollection?>
            = UserDependantLazyProvider { userId -> FirestoreNoteCollection() }
    var tagCollectionProvider: Provider<TagCollection?>
        = UserDependantLazyProvider { userId -> FirestoreTagCollection() }


    /**
     * Provides an instance of something.
     */
    interface Provider<T> {
        val instance: T
    }

    /**
     * Basic provider that stores an initialized instance off the bat.
     */
    class BasicProvider<T>(override val instance: T) : Provider<T>

    /**
     * A provider whose value is only initialized once.
     */
    class LazyProvider<T>(val init: () -> T) : Provider<T> {
        private val _instance: Lazy<T> = lazy(init)
        override val instance: T
            get() = _instance.value
    }

    /**
     * Provider that depends on a user id.
     *
     * @property init Creates an instance of the respective class using the
     * given user id, or returns null if it is not possible. Is only called
     * once per user id.
     */
    class UserDependantLazyProvider<T>(val init: (userId: UUID?) -> T) : Provider<T> {
        private var _instance: T? = null
        private var lastUserId: UUID? = null

        override val instance: T
            get() {
                val userId = Providers.accountStore?.userId
                if (userId != lastUserId) {
                    _instance = init(userId)
                    lastUserId = userId
                }
                @Suppress("UNCHECKED_CAST")
                return _instance as T
            }
    }
}