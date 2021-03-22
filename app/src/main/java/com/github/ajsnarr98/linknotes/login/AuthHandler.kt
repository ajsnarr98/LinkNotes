package com.github.ajsnarr98.linknotes.login

import com.github.ajsnarr98.linknotes.data.UUID
import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface AuthHandler {
    val isSignedIn: Boolean

    /**
     * Optional to implement. Defaults to false.
     */
    val wasSignedInPreviously: Boolean
        get() = false

    /**
     * Optional to implement. Attempts sign in with known credentials.
     *
     * signInResult is called with a userId if successful, or with null if not.
     */
    fun attemptSignIn(signInResult: (userId: UUID?) -> Unit) {
        signInResult(null)
    }

    fun signInWithGoogle(googleAccount: GoogleSignInAccount, signInResult: (userId: UUID?) -> Unit) {
        try {
            signInWithGoogle(googleAccount.idToken!!, signInResult)
        } catch (e: NullPointerException) {
            throw IllegalArgumentException("Invalid google (user id, id token): (${googleAccount.id}, ${googleAccount.idToken}")
        }
    }

    /**
     * Sign in with google given a googleUserId and a googleIdToken.
     *
     * signInResult callback is called with a valid user if successful and null
     * if unsuccessful.
     */
    fun signInWithGoogle(googleIdToken: String, signInResult: (userId: UUID?) -> Unit)
}