package com.github.ajsnarr98.linknotes.network

import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.network.domain.User

interface AuthRepository {
    /**
     * Attempt to sign in with stored user credentials.
     */
    suspend fun attemptSignIn(): ResultStatus<User?>

    /**
     * Attempt to sign in with google.
     */
    suspend fun signInWithGoogle(googleId: String, googleIdToken: String): ResultStatus<User?>

    /**
     * Return success.
     */
    suspend fun signOut(): Boolean

    interface AuthProvider {
        /**
         * Sign in with google and return firebase user id.
         */
        suspend fun signInWithGoogle(googleIdToken: String): ResultStatus<UUID?>

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
        const val USER_FILE = "com.github.ajsnarr98.linknotes.data.local.UserStore"
    }
}