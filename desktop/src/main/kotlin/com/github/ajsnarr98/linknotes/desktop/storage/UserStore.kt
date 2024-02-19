package com.github.ajsnarr98.linknotes.desktop.storage

import com.github.ajsnarr98.linknotes.network.domain.User
import com.github.ajsnarr98.linknotes.network.storage.local.LocalStorage

class UserStore : LocalStorage<User> {
    override suspend fun save(key: String, obj: User?): Boolean {
        return false
    }

    override suspend fun get(key: String): User? {
        return null
    }
}