package com.github.ajsnarr98.linknotes.desktop.login.api

import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.desktop.util.ResourceFileLoader
import com.github.ajsnarr98.linknotes.network.result.ResultStatus
import com.github.ajsnarr98.linknotes.network.auth.AuthRepository
import com.github.ajsnarr98.linknotes.network.auth.FirebaseAuthApi
import com.github.ajsnarr98.linknotes.network.auth.GoogleOAuthApi
import com.github.ajsnarr98.linknotes.network.result.ErrorType
import com.github.ajsnarr98.linknotes.network.util.DispatcherProvider
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp.Browser
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants
import com.google.api.client.http.GenericUrl
import com.google.api.client.json.gson.GsonFactory
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStreamReader

class AuthProviderDesktop(
    private val authApi: FirebaseAuthApi,
    private val googleOAuthApi: GoogleOAuthApi,
    private val dispatcherProvider: DispatcherProvider,
    private val moshi: Moshi,
    private val resourceFileLoader: ResourceFileLoader,
    private val browser: Browser,
) : AuthRepository.AuthProvider {

    companion object {
        private const val PROVIDER_ID = "google.com"

        // TODO - use an actual path provided by something
        private const val TOKENS_DIR_PATH = "tokens"


        private const val ACCESS_TYPE = "offline"
        private const val APPROVAL_PROMPT = "force"

        private const val authorizationServerEncodedUrl: String = GoogleOAuthConstants.AUTHORIZATION_SERVER_URL
        private val tokenServerEncodedUrl: String = GenericUrl(GoogleOAuthConstants.TOKEN_SERVER_URL).build()

        private val scopes: Set<String> = AuthRepository.GOOGLE_SCOPES + "openid email"
    }

    /** Only read in an IO suspend context **/
    private val googleServicesJson: GoogleServicesJson by lazy {
        moshi.adapter(GoogleServicesJson::class.java).fromJson(
            resourceFileLoader.loadAsString("/secrets/google-services.json")
        ) ?: throw JsonDataException("Got null from parser")
    }

    override suspend fun signInWithGoogle(): ResultStatus<UUID?, ErrorType> {
        return withContext(dispatcherProvider.io()) {
            // TODO handle errors
            val (code, redirectUri) = getAuthorizationCodeFromUser()
            val cred = requestAccessAndIdTokenFromAuthorizationCode(authorizationCode = code, redirectUri = redirectUri)

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

    private val linkNotesGoogleOauthSecrets: GoogleClientSecrets by lazy {
        GoogleClientSecrets.load(
            GsonFactory.getDefaultInstance(),
            InputStreamReader(resourceFileLoader.load("/secrets/linknotes_oauth_creds_desktop.json"))
        )
    }

    /**
     * Tries to direct the user to a browser, waits for a corresponding authorization code,
     * and returns it along with the redirect uri.
     *
     * @return (authorizationCode, redirectUri)
     */
    @Throws(IOException::class)
    private suspend fun getAuthorizationCodeFromUser(): Pair<String, String> {
        val receiver =  LocalServerReceiver(port = 8888)

        return receiver.use {
            receiver.initialize()
            val redirectUri: String = receiver.getRedirectUri()
            val authorizationUrl: AuthorizationCodeRequestUrl = GoogleAuthorizationCodeRequestUrl(
                authorizationServerEncodedUrl,
                linkNotesGoogleOauthSecrets.installed.clientId,
                redirectUri,
                scopes,
            )
                .setAccessType(ACCESS_TYPE)
                .setApprovalPrompt(APPROVAL_PROMPT)

            // direct user to default browser to authorize
            browser.browse(authorizationUrl.build())

            // TODO error handling and timeout
            (receiver.waitForCode(-1) as ResultStatus.Success).value to redirectUri
        }
    }

    private suspend fun requestAccessAndIdTokenFromAuthorizationCode(
        authorizationCode: String,
        redirectUri: String,
    ): GoogleOAuthApi.GoogleOAuthTokenResponse {
        return googleOAuthApi.oauthSignIn(
            code = authorizationCode,
            clientSecret = linkNotesGoogleOauthSecrets.installed.clientSecret,
            clientId = linkNotesGoogleOauthSecrets.installed.clientId,
            redirectUri = redirectUri,
        )
    }
}
