package com.github.ajsnarr98.linknotes.network.http

import com.github.ajsnarr98.linknotes.network.logging.LoggingProvider
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

object OkHttpHelper {
    const val TIMEOUT_SECONDS = 90L

    fun buildOkHttpClient(
        loggingProvider: LoggingProvider,
    ): OkHttpClient {
        return OkHttpClient.Builder().apply {
            /* callTimeout is for *overall* length of the call, we don't care about
                the length of the individual parts, so we set them to 0 (infinite), to simplify
                overriding timeouts on a per-call basis. */
            callTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            connectTimeout(0, TimeUnit.SECONDS)
            readTimeout(0, TimeUnit.SECONDS)
            writeTimeout(0, TimeUnit.SECONDS)
            addInterceptor(
                HttpLoggingInterceptor { msg -> loggingProvider.d(msg) }.apply {
                    setLevel(HttpLoggingInterceptor.Level.BODY)
                }
            )

        }.build()

    }
}