package com.github.ajsnarr98.linknotes.network.result

/**
 * Use this when defining a retrofit interface to capture header information
 * in the result type.
 *
 * For example:
 * @GET("myPath")
 * suspend fun getMyThing(
 *     @retrofit2.http.Query("page") page: Int
 * ): ResultStatus<HeaderWrapper<List<MyApiModel>>, ErrorType>
 */
data class HeaderWrapper<T>(
    val body: T,
    val headers: okhttp3.Headers,
)
