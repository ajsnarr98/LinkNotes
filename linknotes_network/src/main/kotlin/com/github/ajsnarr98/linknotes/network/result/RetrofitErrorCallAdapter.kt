package com.github.ajsnarr98.linknotes.network.result

import com.github.ajsnarr98.linknotes.network.logging.LoggingProvider
import com.squareup.moshi.JsonDataException
import okhttp3.Request
import okhttp3.ResponseBody
import okio.Timeout
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import retrofit2.Retrofit
import java.io.IOException
import java.io.InterruptedIOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.UnknownHostException

class RetrofitErrorCallAdapter<S, E : Any>(
    private val successType: Type,
    private val errorTypeAdapter: ErrorTypeAdapter<S, E>,
    private val loggingProvider: LoggingProvider,
    private val isUnitBody: Boolean = false,
) : CallAdapter<S, Call<ResultStatus<S, E>>> {

    /**
     * Represents error type to adapt to.
     */
    sealed interface ErrorTypeAdapter<S, E> {
        /**
         * Adapt to the correct error type given information about the response.
         */
        fun getErrorOnResponse(errorType: ErrorType, call: Call<S>, response: Response<S>): E

        /**
         * Adapt to the correct error type given information about the call failure.
         */
        fun getErrorOnFailure(errorType: ErrorType, call: Call<S>, throwable: Throwable): E

        /**
         * Just pass through [ErrorType] information.
         */
        class Default<S> : ErrorTypeAdapter<S, ErrorType> {
            override fun getErrorOnResponse(errorType: ErrorType, call: Call<S>, response: Response<S>): ErrorType {
                return errorType
            }

            override fun getErrorOnFailure(errorType: ErrorType, call: Call<S>, throwable: Throwable): ErrorType {
                return errorType
            }
        }

        /**
         * Attempt to parse error body along with capturing [ErrorType] information.
         */
        class OptionalBody<S, EB>(
            private val errorConverter: Converter<ResponseBody, EB>,
            private val loggingProvider: LoggingProvider,
        ) : ErrorTypeAdapter<S, ErrorTypeWithBody<EB>> {
            override fun getErrorOnResponse(errorType: ErrorType, call: Call<S>, response: Response<S>): ErrorTypeWithBody<EB> {
                // try to parse error body
                val rawErrorBody = response.errorBody()
                val body: EB? = when {
                    rawErrorBody == null -> null
                    rawErrorBody.contentLength() == 0L -> null
                    else -> try {
                        errorConverter.convert(rawErrorBody)
                    } catch (e: Exception) {
                        // log the exception but keep the existing error type
                        loggingProvider.e(e)
                        null
                    }
                }
                return ErrorTypeWithBody(
                    errorType = errorType,
                    body = body,
                )
            }

            override fun getErrorOnFailure(errorType: ErrorType, call: Call<S>, throwable: Throwable): ErrorTypeWithBody<EB> {
                // we got no usable response
                return ErrorTypeWithBody(errorType = errorType, body = null)
            }
        }
    }

    override fun responseType(): Type = successType

    override fun adapt(call: Call<S>): Call<ResultStatus<S, E>> {
        return NetworkResponseCall(call, errorTypeAdapter, isUnitBody)
    }

    class Factory(
        val loggingProvider: LoggingProvider,
    ) : CallAdapter.Factory() {
        override fun get(
            returnType: Type,
            annotations: Array<Annotation>,
            retrofit: Retrofit,
        ): CallAdapter<*, *>? {
            // suspend functions wrap the response type in `Call`
            if (Call::class.java != getRawType(returnType)) {
                return null
            }

            // check first that the return type is `ParameterizedType`
            check(returnType is ParameterizedType) {
                "return type must be parameterized as ${Call::class.simpleName}<${ResultStatus::class.simpleName}" +
                        "<<Foo, ${ErrorType::class.simpleName}>>," +
                        " ${Call::class.simpleName}<${ResultStatus::class.simpleName}" +
                        "<out Foo, ${ErrorType::class.simpleName}>>, or" +
                        "${Call::class.simpleName}<${ResultStatus::class.simpleName}<${ResultStatus::class.simpleName}" +
                        "<<Foo, ${ErrorTypeWithBody::class.simpleName}<Bar>>, but is $returnType"
            }

            // get the response type inside the `Call` type
            val responseType = getParameterUpperBound(0, returnType)
            // if the response type is not ApiResponse then we can't handle this type, so we return null
            if (getRawType(responseType) != ResultStatus::class.java) {
                return null
            }

            // the response type is ApiResponse and should be parameterized
            check(responseType is ParameterizedType) {
                "Response must be parameterized as ${ResultStatus::class.simpleName}<Foo, ${ErrorType::class.simpleName}>" +
                        " or ${ResultStatus::class.simpleName}<out Foo, ${ErrorType::class.simpleName}>. But is $responseType"
            }

            val successBodyType = getParameterUpperBound(0, responseType)

            val parsedErrorType = getParameterUpperBound(1, responseType)
            val parsedErrorTypeAdapter = errorAdapterForType(parsedErrorType, annotations, retrofit)

            return RetrofitErrorCallAdapter(successBodyType, parsedErrorTypeAdapter, loggingProvider, getRawType(successBodyType) == Unit::class.java)
        }

        /**
         * Gets the error type adapter for this error type, or throws if not supported.
         *
         * @param errorType the error type (E) for the [ResultStatus]
         */
        private fun errorAdapterForType(errorType: Type, annotations: Array<Annotation>, retrofit: Retrofit): ErrorTypeAdapter<out Any, out Any> {
            return when (getRawType(errorType)) {
                ErrorType::class.java -> ErrorTypeAdapter.Default()
                ErrorTypeWithBody::class.java -> run {
                    check(errorType is ParameterizedType) {
                        "${ErrorTypeWithBody::class.simpleName} must be parameterized (e.g. ${ErrorTypeWithBody::class.simpleName}<Foo>)"
                    }
                    // get response parser for error body type
                    val errorBodyType = getParameterUpperBound(0, errorType)
                    ErrorTypeAdapter.OptionalBody<Any, Any>(
                        errorConverter = retrofit.nextResponseBodyConverter(null, errorBodyType, annotations),
                        loggingProvider = loggingProvider,
                    )
                }
                else -> throw IllegalStateException("Error type must be of type: ${ErrorType::class.simpleName} or ${ErrorTypeWithBody::class.simpleName}")
            }
        }
    }

    class NetworkResponseCall<S, E : Any>(
        private val delegate: Call<S>,
        private val errorTypeAdapter: ErrorTypeAdapter<S, E>,
        private val isUnitBody: Boolean= false,
    ) : Call<ResultStatus<S, E>> {
        override fun enqueue(callback: Callback<ResultStatus<S, E>>) {
            return delegate.enqueue(object : Callback<S> {
                override fun onResponse(call: Call<S>, response: Response<S>) {
                    var body = response.body()
                    val code = response.code()

                    if (response.isSuccessful) {
                        if (body is HeaderWrapper<*>) {
                            body = body.copy(headers = response.headers()) as S
                        }
                        if (body != null && !isUnitBody) {
                            callback.onResponse(
                                this@NetworkResponseCall,
                                Response.success(ResultStatus.Success(body))
                            )
                        } else if (isUnitBody) {
                            callback.onResponse(
                                this@NetworkResponseCall,
                                Response.success(ResultStatus.Success(Unit as S))
                            )
                        } else {
                            val cause = IOException("Response body was null")
                            callback.onResponse(
                                this@NetworkResponseCall,
                                Response.success(
                                    ResultStatus.Error(
                                        errorTypeAdapter.getErrorOnResponse(
                                            ErrorType.UnexpectedResponse(code = code, cause = cause),
                                            call,
                                            response,
                                        )
                                    )
                                )
                            )
                        }
                    } else {
                        callback.onResponse(
                            this@NetworkResponseCall,
                            Response.success(
                                ResultStatus.Error(
                                    errorTypeAdapter.getErrorOnResponse(
                                        ErrorType.forHttpCode(code),
                                        call,
                                        response,
                                    )
                                )
                            )
                        )
                    }
                }

                override fun onFailure(call: Call<S>, throwable: Throwable) {
                    val networkResponse: ErrorType = when (throwable) {
                        is UnknownHostException -> ErrorType.NoNetwork(cause = throwable)
                        is InterruptedIOException -> ErrorType.Timeout(cause = throwable)
                        is JsonDataException -> ErrorType.UnexpectedResponse(code = null, cause = throwable)
                        is IllegalArgumentException -> throw throwable
                        else -> ErrorType.UnexpectedResponse(code = null, cause = throwable)
                    }
                    callback.onResponse(this@NetworkResponseCall, Response.success(
                        ResultStatus.Error(
                            errorTypeAdapter.getErrorOnFailure(
                                networkResponse,
                                call,
                                throwable
                            )
                        )
                    ))
                }
            })
        }

        override fun isExecuted() = delegate.isExecuted

        override fun clone() = NetworkResponseCall(delegate.clone(), errorTypeAdapter)

        override fun isCanceled() = delegate.isCanceled

        override fun cancel() = delegate.cancel()

        override fun execute(): Response<ResultStatus<S, E>> {
            throw UnsupportedOperationException("RetrofitErrorCallAdapter doesn't support execute")
        }

        override fun request(): Request = delegate.request()

        override fun timeout(): Timeout = delegate.timeout()
    }

}