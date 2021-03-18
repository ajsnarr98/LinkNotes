package com.github.ajsnarr98.linknotes.data.local

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

/**
 * Persists basic information about a user's account
 */
class AccountStore(app: Application) {

    private val prefs: SharedPreferences
            = app.getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE)

    private val editor: SharedPreferences.Editor
        get() = prefs.edit()

    /**
     * User id that was last signed in.
     */
    val userId: String?
        get() = prefs.getString(USER_ID_KEY, null)

    val googleUserId: String?
        get() = prefs.getString(GOOGLE_ID_KEY, null)


    fun persistUserId(value: String) {
        editor.putString(USER_ID_KEY, value)
    }

    fun clearUserId() {
        editor.remove(USER_ID_KEY)
    }

    fun persistGoogleUserId(value: String) {
        editor.putString(GOOGLE_ID_KEY, value)
    }

    fun clearGoogleUserId() {
        editor.remove(GOOGLE_ID_KEY)
    }

    companion object {
        const val PREFS_FILE = "com.github.ajsnarr98.linknotes.data.local.PersistentStore"

        const val USER_ID_KEY =  "user_id"
        const val GOOGLE_ID_KEY = "google_user_id"
    }
}