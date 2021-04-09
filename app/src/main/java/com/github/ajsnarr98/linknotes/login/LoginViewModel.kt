package com.github.ajsnarr98.linknotes.login

import android.app.Activity
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.github.ajsnarr98.linknotes.App
import com.github.ajsnarr98.linknotes.Providers
import com.github.ajsnarr98.linknotes.R
import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.data.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions

/**
 * If passed in noteID is null, creates a new note.
 */
class LoginViewModel(app: Application) : AndroidViewModel(app) {

    private val accountStore = Providers.accountStore
    private val authHandler: AuthHandler = Providers.authHandler ?: throw IllegalStateException("No auth handler instance")

    // Configure sign-in to request the user's ID, email address, and basic
    // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
    private val gso: GoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(getApplication<App>().getString(R.string.server_client_id))
        .build()

    val isSignedIn: Boolean
        get() = authHandler.isSignedIn

    fun googleSignInClient(activity: Activity): GoogleSignInClient = GoogleSignIn.getClient(activity, gso)

    /**
     * Signs in using the given google account. Stores relevant info in the
     * accountStore.
     */
    fun signInWithGoogleAccount(googleAccount: GoogleSignInAccount, signInResultListener: (success: Boolean) -> Unit) {
        // expect id token because we called requestIdToken when
        // creating our GoogleSignInOptions
        requireNotNull(googleAccount.idToken, { "Invalid (missing) google id token" })
        authHandler.signInWithGoogle(googleAccount.idToken!!) { userId: UUID? ->
            if (userId != null) {
                // save user info to account store
                accountStore?.persistUserInfo(User(
                    id = userId,
                    googleID = googleAccount.id,
                    googleIDToken = googleAccount.idToken,
                ))
            }
            signInResultListener(userId != null)
        }
    }

    /**
     * If passed in inNote is null, creates a new note.
     */
    class Factory(private val app: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                LoginViewModel(app) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }
}
