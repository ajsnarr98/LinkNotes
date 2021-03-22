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

        binding.loadingIndicator.visibility = View.VISIBLE
        binding.googleSignIn.visibility = View.GONE

        binding.googleSignIn.setSize(SignInButton.SIZE_STANDARD);
        binding.googleSignIn.setOnClickListener {
            startActivityForResult(viewModel.googleSignInClient.signInIntent, RC_SIGN_IN)
        }
    }

    override fun onStart() {
        super.onStart()
        if (viewModel.isSignedIn) {
            Timber.i("already signed in, continuing through login screen.")
            startActivity(Router.postLoginIntent(this))
        } else {
            // try to do a silent sign in
            Timber.i("attempting silent sign in")
            val silentSignInResult = viewModel.googleSignInClient.silentSignIn()
            if (silentSignInResult.isSuccessful) {
                // immediate result ready
                handleGoogleSignInResult(silentSignInResult)
            } else {
                // wait for result
                silentSignInResult.addOnCompleteListener { result ->
                    handleGoogleSignInResult(result)
                }
            }
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
                Timber.i("attempting to sign into firebase with google")
                viewModel.signInWithGoogleAccount(account) { success ->
                    if (success) {
                        startActivity(Router.postLoginIntent(this))
                    } else {
                        Timber.e("sign in to firebase with google failed")
                        // make sure sign in button is now visible
                        binding.googleSignIn.visibility = View.VISIBLE
                        binding.loadingIndicator.visibility = View.GONE
                    }
                }
            } else {
                // make sure sign in button is now visible
                binding.googleSignIn.visibility = View.VISIBLE
                binding.loadingIndicator.visibility = View.GONE
            }
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Timber.e("signInResult:failed code=${e.statusCode}")
            // make sure sign in button is now visible
            binding.googleSignIn.visibility = View.VISIBLE
            binding.loadingIndicator.visibility = View.GONE
        }
    }
}