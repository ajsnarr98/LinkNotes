package com.github.ajsnarr98.linknotes.login

import android.app.Activity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.ajsnarr98.linknotes.Providers
import com.github.ajsnarr98.linknotes.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

/**
 * If passed in noteID is null, creates a new note.
 */
class LoginViewModel(activity: Activity) : AndroidViewModel(activity.application) {

    private val accountStore = Providers.accountStore

    // Configure sign-in to request the user's ID, email address, and basic
    // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
    private val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(activity.getString(R.string.server_client_id))
        .build()
    val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(activity, gso)

    val isSignedIn: Boolean
        get() {
            val account = GoogleSignIn.getLastSignedInAccount(getApplication())
            return account != null
                    && accountStore?.userId != null
                    && account.id != null
                    && account.id == accountStore.googleUserId
        }

    /**
     * Signs in using the given google account. Stores relevant info in the
     * accountStore.
     */
    fun signInWithGoogleAccount(googleAccount: GoogleSignInAccount) {

    }

    /**
     * If passed in inNote is null, creates a new note.
     */
    class Factory(private val activity: Activity) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                LoginViewModel(activity) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}
