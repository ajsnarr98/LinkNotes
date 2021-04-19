package com.github.ajsnarr98.linknotes

import com.github.ajsnarr98.linknotes.data.NoteCollection
import com.github.ajsnarr98.linknotes.data.TagCollection
import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreNoteCollection
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreTagCollection
import com.github.ajsnarr98.linknotes.data.local.AccountStore
import com.github.ajsnarr98.linknotes.data.local.UnsavedChangeStore
import com.github.ajsnarr98.linknotes.login.AuthHandler
import com.github.ajsnarr98.linknotes.login.FirebaseAuthHandler
import java.lang.IllegalStateException

/**
 * An object that provides default implementations (global scope) during runtime.
 */
object Providers {
    val accountStore: AccountStore?
        get() = accountStoreProvider.instance

    val authHandler: AuthHandler?
        get() = authHandlerProvider.instance

    val unsavedChangeStore: UnsavedChangeStore?
        get() = unsavedChangeStoreProvider.instance

    val noteCollection: NoteCollection?
        get() = noteCollectionProvider.instance

    val tagCollection: TagCollection?
        get() = tagCollectionProvider.instance

    // default provider implementations
    var accountStoreProvider: Provider<AccountStore?>
        = BasicProvider(null)
    var authHandlerProvider: Provider<AuthHandler?>
        = RepeatProvider { FirebaseAuthHandler() }
    var unsavedChangeStoreProvider: Provider<UnsavedChangeStore?>
        = BasicProvider(null)
    var noteCollectionProvider: Provider<NoteCollection?>
        = UserDependantLazyProvider { userId ->
            FirestoreNoteCollection(userId = userId ?: throw IllegalStateException("Cannot access notes for null user"))
        }
    var tagCollectionProvider: Provider<TagCollection?>
        = UserDependantLazyProvider { userId ->
            FirestoreTagCollection(userId = userId ?: throw IllegalStateException("Cannot access tags for null user"))
        }

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
     * Initializes a new instance using given initializer block whenever
     * this provider is called.
     */
    class RepeatProvider<T>(val initializer: () -> T) : Provider<T> {
        override val instance: T
            get() = initializer()
    }

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
        private var isInitialized: Boolean = false
        private var _instance: T? = null
        private var lastUserId: UUID? = null

        override val instance: T
            get() {
                val userId = Providers.accountStore?.userId
                if (!isInitialized || userId != lastUserId) {
                    _instance = init(userId)
                    lastUserId = userId
                    isInitialized = true
                }
                @Suppress("UNCHECKED_CAST")
                return _instance as T
            }
    }
}