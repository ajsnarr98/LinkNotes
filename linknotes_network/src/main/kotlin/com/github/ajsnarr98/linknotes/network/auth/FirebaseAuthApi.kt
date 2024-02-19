package com.github.ajsnarr98.linknotes.network.auth

import com.squareup.moshi.JsonClass
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

interface FirebaseAuthApi {

    companion object {
        const val BASE_URL = "https://identitytoolkit.googleapis.com"
    }

    @POST("v1/accounts:signInWithIdp")
    suspend fun oauthSignIn(
        @Query("key") apiKey: String,
        @Body request: OAuthRequest,
    ): OAuthResponse

    @JsonClass(generateAdapter = true)
    data class OAuthRequest(
        val requestUri: String,
        val postBody: String,
        val returnSecureToken: Boolean = true,
        val returnIdpCredential: Boolean,
    )

    @JsonClass(generateAdapter = true)
    data class OAuthResponse(
        val firstName: String? = null,
        val lastName: String? = null,
        val fullName: String? = null,
        val localId: String? = null,
    )
}