package com.github.ajsnarr98.linknotes.desktop.login.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleTokenResponse(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "refresh_token") val refreshToken: String,
    @Json(name = "expires_in") val expiresInSeconds: Long,
    @Json(name = "scope") val scope: String,
    @Json(name = "token_type") val tokenType: String,
    @Json(name = "id_token") val idToken: String,
)
