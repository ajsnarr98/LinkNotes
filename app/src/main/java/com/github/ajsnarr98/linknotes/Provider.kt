package com.github.ajsnarr98.linknotes

import com.github.ajsnarr98.linknotes.data.local.AccountStore

/**
 * An object that provides default implementations (global scope) during runtime.
 */
object Provider {
    lateinit var accountStore: AccountStore
}