package com.github.ajsnarr98.linknotes.login

import com.github.ajsnarr98.linknotes.Providers
import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.data.local.AccountStore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

/**
 * Meant to be used inside a viewmodel.
 */
class FirebaseAuthHandler(
    private val accountStore: AccountStore = Providers.accountStore ?: throw IllegalStateException("AccountStore was null"),
) : AuthHandler {

    // init firebase auth instance
    private val auth: FirebaseAuth = Firebase.auth

    override val isSignedIn: Boolean
        get() = auth.currentUser != null

    override val wasSignedInPreviously: Boolean
        get() = accountStore.hasSignedInInfo

    override fun attemptSignIn(signInResult: (userId: UUID?) -> Unit) {
        if (accountStore.validateStoredSignInCredentials()) {
            val googleIdToken = accountStore.googleUserIdToken
            when {
                googleIdToken != null -> signInWithGoogle(googleIdToken, signInResult)
                else -> signInResult(null)
            }
        } else {
            signInResult(null)
        }
    }

    override fun signInWithGoogle(googleIdToken: String, signInResult: (userId: UUID?) -> Unit) {
        val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Timber.d("signInWithCredential:success")
                    val firebaseUser: FirebaseUser = auth.currentUser ?: throw IllegalStateException("User should not be null")
                    // report success
                    signInResult(firebaseUser.uid)
                } else {
                    Timber.e("signInWithCredential:failure")
                    Timber.e( task.exception)
                    // report failure
                    signInResult(null)
                }
            }
    }
}