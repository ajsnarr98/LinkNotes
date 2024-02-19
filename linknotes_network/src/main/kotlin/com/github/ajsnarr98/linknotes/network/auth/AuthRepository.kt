package com.github.ajsnarr98.linknotes.network.auth

import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.network.ResultStatus
import com.github.ajsnarr98.linknotes.network.domain.User

interface AuthRepository {

    /**
     * Attempt to sign in with stored user credentials.
     */
    suspend fun attemptSignIn(): ResultStatus<User?>

    /**
     * Attempt to sign in with google.
     */
    suspend fun signInWithGoogle(): ResultStatus<User?>

    /**
     * Return success.
     */
    suspend fun signOut(): Boolean

    interface AuthProvider {
        /**
         * Sign in with google and return firebase user id.
         */
        suspend fun signInWithGoogle(): ResultStatus<UUID?>

        /**
         * Find validity of given google id token.
         */
        suspend fun isValidGoogleToken(googleIdToken: String): Boolean

        /**
         * Perform any actions needed to sign out.
         */
        suspend fun signOut(): Boolean
    }

    companion object {
        private const val GOOGLE_PHOTOS_READONLY_SCOPE = "https://www.googleapis.com/auth/photoslibrary.readonly"

        val GOOGLE_SCOPES: Set<String> = setOf(GOOGLE_PHOTOS_READONLY_SCOPE)

        const val USER_FILE = "com.github.ajsnarr98.linknotes.data.local.UserStore"
    }
}