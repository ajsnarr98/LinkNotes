package com.github.ajsnarr98.linknotes.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.ajsnarr98.linknotes.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    companion object {
        // this can be any value
        private const val RC_SIGN_IN = 42

        fun getLoginIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, LoginViewModel.Factory(this))
            .get(LoginViewModel::class.java)

        binding.googleSignIn.setSize(SignInButton.SIZE_STANDARD);
        binding.googleSignIn.setOnClickListener {
            startActivityForResult(viewModel.googleSignInClient.signInIntent, RC_SIGN_IN)
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.isSignedIn) {
            startActivity(Router.postLoginIntent(this))
        } else {
            // if not signed in, prepare to sign in
            binding.googleSignIn.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleGoogleSignInResult(task)
        }
    }

    private fun handleGoogleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)

            if (account != null) {
                // Signed in successfully, show authenticated UI.
                viewModel.signInWithGoogleAccount(account)
                // TODO - observe and wait for sign in to finish before continuing
                startActivity(Router.postLoginIntent(this))
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Timber.e("signInResult:failed code=${e.statusCode}")
            // TODO - show error
        }
    }
}