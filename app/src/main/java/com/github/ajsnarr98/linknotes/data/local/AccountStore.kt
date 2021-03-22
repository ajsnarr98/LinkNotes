package com.github.ajsnarr98.linknotes.data.local

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.github.ajsnarr98.linknotes.R
import com.github.ajsnarr98.linknotes.data.User
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory

/**
 * Persists basic information about a user's account
 */
class AccountStore(app: Application) {

    private val ANDROID_CLIENT_ID = app.getString(R.string.android_client_id)

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

    val googleUserIdToken: String?
        get() = prefs.getString(GOOGLE_ID_TOKEN_KEY, null)


    /**
     * True if a user was signed in previously (or currently) and has info stored.
     *
     * Does not validate sign in credentials.
     */
    val hasSignedInInfo
        get() = userId != null &&
                (googleUserId != null && googleUserIdToken != null)

    fun validateStoredSignInCredentials(): Boolean {
            if (userId != null) {
                if (googleUserId != null && googleUserIdToken != null) {
                    // check if google id token is currently valid
                    val googleVerifier =
                        GoogleIdTokenVerifier.Builder(NetHttpTransport(), GsonFactory())
                            .setAudience(listOf(ANDROID_CLIENT_ID))
                            .build()
                    val googleIdToken = googleVerifier.verify(googleUserIdToken)
                    if (googleIdToken != null) {
                        return true
                    }
                }
            }

            return false
        }

    /**
     * Deletes any user fields that are null, and updates fields that are
     * non-null.
     */
    fun persistUserInfo(user: User?) {
        if (user?.id == null) {
            clearUserId()
        } else {
            persistUserId(user.id)
        }

        if (user?.googleID == null) {
            clearGoogleUserId()
        } else {
            persistGoogleUserId(user.googleID)
        }

        if (user?.googleIDToken == null) {
            clearGoogleUserIdToken()
        } else {
            persistGoogleUserIdToken(user.googleIDToken)
        }
    }

    /**
     * Deletes all user fields.
     */
    fun clearUserInfo() {
        persistUserInfo(null)
    }

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

    fun persistGoogleUserIdToken(value: String) {
        editor.putString(GOOGLE_ID_TOKEN_KEY, value)
    }

    fun clearGoogleUserIdToken() {
        editor.remove(GOOGLE_ID_TOKEN_KEY)
    }

    companion object {
        const val PREFS_FILE = "com.github.ajsnarr98.linknotes.data.local.PersistentStore"

        const val USER_ID_KEY =  "user_id"
        const val GOOGLE_ID_KEY = "google_user_id"
        const val GOOGLE_ID_TOKEN_KEY = "google_user_id_token"
    }
}