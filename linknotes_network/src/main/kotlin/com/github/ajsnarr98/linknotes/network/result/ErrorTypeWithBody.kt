package com.github.ajsnarr98.linknotes.network.result

/**
 * Use this when defining a retrofit interface to capture an error body.
 *
 * For example:
 * @GET("myPath")
 * suspend fun getMyThing(
 *     @retrofit2.http.Query("page") page: Int
 * ): ResultStatus<List<MyApiModel>, ErrorTypeWithBody<MyErrorApiModel>>
 */
data class ErrorTypeWithBody<T>(
    val errorType: ErrorType,
    val body: T?,
)
