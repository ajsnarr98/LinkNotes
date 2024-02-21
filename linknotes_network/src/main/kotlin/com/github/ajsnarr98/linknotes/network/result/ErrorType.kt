package com.github.ajsnarr98.linknotes.network.result

import java.net.HttpURLConnection

sealed interface ErrorType {
    val code: Int?
    val cause: Throwable?

    data class NoNetwork(
        override val code: Int? = null,
        override val cause: Throwable? = null,
    ) : ErrorType

    data class Timeout(
        override val code: Int? = null,
        override val cause: Throwable? = null,
    ) : ErrorType

    data class ServerError(
        override val code: Int? = null,
        override val cause: Throwable? = null,
    ) : ErrorType

    data class BadRequest(
        override val code: Int? = null,
        override val cause: Throwable? = null,
    ) : ErrorType

    data class OtherErrorCode(
        override val code: Int? = null,
        override val cause: Throwable? = null,
    ) : ErrorType

    data class UnexpectedResponse(
        override val code: Int? = null,
        override val cause: Throwable? = null,
    ) : ErrorType

    companion object {
        /**
         * Normal logic based on error codes.
         */
        fun forHttpCode(code: Int): ErrorType {
            return when (code) {
                in (400 until 500) -> BadRequest(code, cause = null)
                in (500 until 600) -> ServerError(code, cause = null)
                else -> OtherErrorCode(code = code, cause = null)
            }
        }
    }
}