package com.github.ajsnarr98.linknotes.desktop.login.api

import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.network.ResultStatus
import com.github.ajsnarr98.linknotes.network.auth.AuthRepository
import com.github.ajsnarr98.linknotes.network.auth.FirebaseAuthApi
import com.github.ajsnarr98.linknotes.network.util.DispatcherProvider
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow
import com.google.api.client.auth.oauth2.AuthorizationCodeRequestUrl
import com.google.api.client.auth.oauth2.TokenResponse
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp.Browser
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp.DefaultBrowser
import com.google.api.client.extensions.java6.auth.oauth2.VerificationCodeReceiver
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.util.Preconditions
import com.google.auth.oauth2.UserCredentials
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.util.stream.Collectors


class AuthProviderDesktop(
    private val authApi: FirebaseAuthApi,
    private val dispatcherProvider: DispatcherProvider,
    private val googleOAuth: GoogleOAuth,
) : AuthRepository.AuthProvider {

    companion object {
        /** Only read in an IO suspend context **/
//        private val firebaseApiKey: String by lazy {
//            val path = "secrets/firebase_api_key"
//            BufferedReader(
//                InputStreamReader(
//                    javaClass.classLoader.getResourceAsStream(path)
//                        ?: throw FileNotFoundException("Resource not found: $path")
//                )
//            ).use {
//                it.lines().collect(Collectors.joining("\n")).trim()
//            }
//        }


        private const val PROVIDER_ID = "google.com"

        // TODO - use an actual path provided by something
        private const val TOKENS_DIR_PATH = "tokens"

    }

    /** Only read in an IO suspend context **/
//    private val linkNotesGoogleOauthSecrets: GoogleClientSecrets by lazy {
//        val path = "secrets/linknotes_oauth_creds_desktop.json"
//        GoogleClientSecrets.load(
//            jsonFactory,
//            InputStreamReader(
//                javaClass.classLoader.getResourceAsStream(path)
//                        ?: throw FileNotFoundException("Resource not found: $path")
//            )
//        )
//    }



    override suspend fun signInWithGoogle(): ResultStatus<UUID?> {
        return withContext(dispatcherProvider.io()) {
            val cred = googleOAuth.authorizeUsingDefaultBrowser()

            val oauthResponse = authApi.oauthSignIn(
                apiKey = firebaseApiKey,
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
