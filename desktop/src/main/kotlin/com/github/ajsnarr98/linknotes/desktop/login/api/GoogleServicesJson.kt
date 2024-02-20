package com.github.ajsnarr98.linknotes.desktop.login.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GoogleServicesJson(
    @Json(name = "project_info") val projectInfo: ProjectInfo,
    @Json(name = "client") val client: List<Client>,
    @Json(name = "configuration_version") val configurationVersion: String,
) {
    @JsonClass(generateAdapter = true)
    data class ProjectInfo(
        @Json(name = "project_number") val projectNumber: String,
        @Json(name = "firebase_url") val firebaseUrl: String,
        @Json(name = "project_id") val projectId: String,
        @Json(name = "storage_bucket") val storageBucket: String,
    )

    @JsonClass(generateAdapter = true)
    data class Client(
        @Json(name = "client_info") val clientInfo: Info,
        @Json(name = "oauth_client") val oAuthClient: List<String>,
        @Json(name = "api_key") val apiKey: List<ApiKey>,
        @Json(name = "services") val services: Services,
    ) {
        @JsonClass(generateAdapter = true)
        data class Info(
            @Json(name = "mobilesdk_app_id") val mobileSdkAppId: String,
            @Json(name = "android_client_info") val androidClientInfo: Android,
        ) {
            @JsonClass(generateAdapter = true)
            data class Android(
                @Json(name = "package_name") val packageName: String,
            )
        }

        @JsonClass(generateAdapter = true)
        data class ApiKey(
            @Json(name = "current_key") val currentKey: String,
        )

        @JsonClass(generateAdapter = true)
        data class Services(
            @Json(name = "appinvite_service") val appInviteService: AppInviteService,
        ) {
            @JsonClass(generateAdapter = true)
            data class AppInviteService(
                @Json(name = "other_platform_oauth_client") val otherPlatformOAuthClient: List<String>,
            )
        }
    }
}
