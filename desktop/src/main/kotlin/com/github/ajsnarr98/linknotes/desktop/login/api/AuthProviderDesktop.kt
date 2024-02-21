package com.github.ajsnarr98.linknotes.desktop.login.api

import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.desktop.util.ResourceFileLoader
import com.github.ajsnarr98.linknotes.network.result.ResultStatus
import com.github.ajsnarr98.linknotes.network.auth.AuthRepository
import com.github.ajsnarr98.linknotes.network.auth.FirebaseAuthApi
import com.github.ajsnarr98.linknotes.network.result.ErrorType
import com.github.ajsnarr98.linknotes.network.util.DispatcherProvider
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import kotlinx.coroutines.withContext

class AuthProviderDesktop(
    private val authApi: FirebaseAuthApi,
    private val dispatcherProvider: DispatcherProvider,
    private val googleOAuth: GoogleOAuth,
    private val moshi: Moshi,
    private val resourceFileLoader: ResourceFileLoader,
) : AuthRepository.AuthProvider {

    companion object {
        private const val PROVIDER_ID = "google.com"

        // TODO - use an actual path provided by something
        private const val TOKENS_DIR_PATH = "tokens"
    }

    /** Only read in an IO suspend context **/
    private val googleServicesJson: GoogleServicesJson by lazy {
        moshi.adapter(GoogleServicesJson::class.java).fromJson(
            resourceFileLoader.loadAsString("/secrets/google-services.json")
        ) ?: throw JsonDataException("Got null from parser")
    }

    override suspend fun signInWithGoogle(): ResultStatus<UUID?, ErrorType> {
        return withContext(dispatcherProvider.io()) {
            val cred = googleOAuth.authorizeUsingDefaultBrowser()

            val oauthResponse = authApi.oauthSignIn(
                apiKey = googleServicesJson.client.first().apiKey.first().currentKey,
                request = FirebaseAuthApi.OAuthRequest(
                    requestUri = "http://localhost",
                    postBody = "id_token=${cred.idToken}&providerId=$PROVIDER_ID",
                    returnIdpCredential = false,
                ),
            )

            return@withContext ResultStatus.Success(oauthResponse.localId)
        }
    }

    override suspend fun isValidGoogleToken(googleIdToken: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun signOut(): Boolean {
        TODO("Not yet implemented")
    }
}
