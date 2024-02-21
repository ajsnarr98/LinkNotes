package com.github.ajsnarr98.linknotes.desktop.login.api

import com.github.ajsnarr98.linknotes.desktop.util.ResourceFileLoader
import com.github.ajsnarr98.linknotes.network.auth.AuthRepository
import com.github.ajsnarr98.linknotes.network.auth.GoogleOAuthApi
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeRequestUrl
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.auth.oauth2.GoogleOAuthConstants
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.GenericUrl
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import java.io.IOException
import java.io.InputStreamReader

class RealGoogleOAuth(
    private val jsonFactory: JsonFactory,
    private val googleOAuthApi: GoogleOAuthApi,
    private val resourceFileLoader: ResourceFileLoader,
) : GoogleOAuth {

    companion object {
        private const val ACCESS_TYPE = "offline"
        private const val APPROVAL_PROMPT = "force"

        private const val authorizationServerEncodedUrl: String = GoogleOAuthConstants.AUTHORIZATION_SERVER_URL
        private val tokenServerEncodedUrl: String = GenericUrl(GoogleOAuthConstants.TOKEN_SERVER_URL).build()

        private val scopes: Set<String> = AuthRepository.GOOGLE_SCOPES + "openid email"
    }

    /** Only read in an IO suspend context **/
    private val linkNotesGoogleOauthSecrets: GoogleClientSecrets by lazy {
        GoogleClientSecrets.load(
            jsonFactory,
            InputStreamReader(resourceFileLoader.load("/secrets/linknotes_oauth_creds_desktop.json"))
        )
    }

    override suspend fun authorizeUsingDefaultBrowser(): GoogleOAuthApi.GoogleTokenResponse {
        // TODO handle errors
        val (code, redirectUri) = getAuthorizationCodeFromUser()
        return requestAccessAndIdTokenFromAuthorizationCode(authorizationCode = code, redirectUri = redirectUri)
    }

    /**
     * Tries to direct the user to a browser, waits for a corresponding authorization code,
     * and returns it along with the redirect uri.
     *
     * @return (authorizationCode, redirectUri)
     */
    @Throws(IOException::class)
    private suspend fun getAuthorizationCodeFromUser(): Pair<String, String> {
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        val browser = AuthorizationCodeInstalledApp.DefaultBrowser()

        return try {
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

            receiver.waitForCode() to redirectUri
        } finally {
            receiver.stop()
        }
    }

    @Throws(IOException::class)
    private suspend fun requestAccessAndIdTokenFromAuthorizationCode(
        authorizationCode: String,
        redirectUri: String,
    ): GoogleOAuthApi.GoogleTokenResponse {
        return googleOAuthApi.oauthSignIn(
            code = authorizationCode,
            clientSecret = linkNotesGoogleOauthSecrets.installed.clientSecret,
            clientId = linkNotesGoogleOauthSecrets.installed.clientId,
            redirectUri = redirectUri,
        )
    }
}

interface GoogleOAuth {
    /**
     * Uses the default browser to ask the user for an oauth code.
     */
    suspend fun authorizeUsingDefaultBrowser(): GoogleOAuthApi.GoogleTokenResponse
}