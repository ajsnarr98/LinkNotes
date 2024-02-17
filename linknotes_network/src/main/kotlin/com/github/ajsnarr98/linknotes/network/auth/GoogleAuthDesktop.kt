package com.github.ajsnarr98.linknotes.network.auth

import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.network.ResultStatus
import com.github.ajsnarr98.linknotes.network.util.DispatcherProvider
import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.JsonFactory
import com.google.api.client.json.gson.GsonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.util.stream.Collectors

class GoogleAuthDesktop(
//    val authApi: FirebaseAuthApi,
    private val dispatcherProvider: DispatcherProvider,
    private val jsonFactory: JsonFactory = GsonFactory.getDefaultInstance(),
) : AuthRepository.AuthProvider {

    companion object {
        /** Only read in an IO suspend context **/
        private val firebaseApiKey: String by lazy {
            val path = "secrets/firebase_api_key"
            BufferedReader(
                InputStreamReader(
                    javaClass.classLoader.getResourceAsStream(path)
                        ?: throw FileNotFoundException("Resource not found: $path")
                )
            ).use {
                it.lines().collect(Collectors.joining("\n")).trim()
            }
        }

        private const val PROVIDER_ID = "google.com"
        private const val GOOGLE_PHOTOS_READONLY_SCOPE = "https://www.googleapis.com/auth/photoslibrary.readonly"

        // TODO - use an actual path provided by something
        private const val TOKENS_DIR_PATH = "tokens"
    }

    /** Only read in an IO suspend context **/
    private val linkNotesGoogleOauthSecrets: GoogleClientSecrets by lazy {
        val path = "secrets/linknotes_oauth_creds_desktop.json"
        GoogleClientSecrets.load(
            jsonFactory,
            InputStreamReader(
                javaClass.classLoader.getResourceAsStream(path)
                        ?: throw FileNotFoundException("Resource not found: $path")
            )
        )
    }

    override suspend fun signInWithGoogle(googleIdToken: String): ResultStatus<UUID?> {
//        AuthorizationCodeIns
//        authApi.oauthSignIn(
//            apiKey = firebaseApiKey,
//            request = FirebaseAuthApi.OAuthRequest(
//                requestUri = ,
//                postBody = "id_token=${googleIdToken}&providerId=${PROVIDER_ID}",
//                returnIdpCredential = false,
//            ),
//        )
        val receiver = LocalServerReceiver.Builder().setPort(8888).build()
        TODO()
    }

    /**
     * Creates an authorized Credential object.
     * @param httpTransport The network HTTP Transport.
     * @return An authorized Credential object.
     * @throws IOException If the credentials.json file cannot be found.
     */
    @Throws(IOException::class)
    private suspend fun getCredentials(
        httpTransport: NetHttpTransport = GoogleNetHttpTransport.newTrustedTransport()
    ): Credential {
        return withContext(dispatcherProvider.io()) {
            // Build flow and trigger user authorization request.
            val flow = GoogleAuthorizationCodeFlow.Builder(
                httpTransport, jsonFactory, linkNotesGoogleOauthSecrets, listOf(GOOGLE_PHOTOS_READONLY_SCOPE)
            )
                .setDataStoreFactory(FileDataStoreFactory(File(TOKENS_DIR_PATH)))
                .setAccessType("offline")
                .build()
            val receiver = LocalServerReceiver.Builder().setPort(8888).build()
            return@withContext AuthorizationCodeInstalledApp(flow, receiver).authorize("user")
        }
    }

    override suspend fun isValidGoogleToken(googleIdToken: String): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun signOut(): Boolean {
        TODO("Not yet implemented")
    }

}
