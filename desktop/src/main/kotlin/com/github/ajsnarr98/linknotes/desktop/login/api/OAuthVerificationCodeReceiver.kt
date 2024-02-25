package com.github.ajsnarr98.linknotes.desktop.login.api

import com.github.ajsnarr98.linknotes.network.result.ResultStatus
import java.io.IOException

interface OAuthVerificationCodeReceiver : AutoCloseable {

    /**
     * @throws IOException
     */
    @Throws(IOException::class)
    fun initialize()

    /**
     * Returns the redirect URI.
     */
    fun getRedirectUri(): String

    /**
     * Waits for a verification code with a timeout.
     *
     * @param timeoutMillis timeout in milliseconds, or -1 if no timeout
     * @throws IOException
     */
    @Throws(IOException::class)
    suspend fun waitForCode(timeoutMillis: Long): ResultStatus<String, String>
}