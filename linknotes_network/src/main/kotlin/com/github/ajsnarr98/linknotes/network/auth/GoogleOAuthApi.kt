package com.github.ajsnarr98.linknotes.network.auth

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GoogleOAuthApi {
    companion object {
        const val BASE_URL = "https://oauth2.googleapis.com"
    }

    /**
     * Use an oauth authorization code to request access/refresh tokens for
     * a Google account.
     *
     * @param authorizationCode authorizationCode
     * @param code This is the URL decoded value obtained from the browser request in the previous step.
     * @param client_secret This comes from the client definition created in the second step and must match exactly.
     * @param client_id This comes from the client definition created in the second step and must match exactly.
     * @param redirect_uri  This comes from the client definition created in the second step and must match exactly.
     *                      You can use the home page of your website for example, it doesn’t matter much what it
     *                      is as long as it matches your definition.
     */
    @FormUrlEncoded
    @POST("/token")
    suspend fun oauthSignIn(
        @Field("grant_type") grantType: String = "authorization_code",
        @Field("code") code: String,
        @Field("client_secret") clientSecret: String,
        @Field("client_id") clientId: String,
        @Field("redirect_uri") redirectUri: String,
    ): GoogleTokenResponse

    @JsonClass(generateAdapter = true)
    data class GoogleTokenResponse(
        @Json(name = "access_token") val accessToken: String,
        @Json(name = "refresh_token") val refreshToken: String,
        @Json(name = "expires_in") val expiresInSeconds: Long,
        @Json(name = "scope") val scope: String,
        @Json(name = "token_type") val tokenType: String,
        @Json(name = "id_token") val idToken: String,
    )
}