package com.github.ajsnarr98.linknotes.network.result

sealed interface ResultStatus<out T, out E> : java.io.Serializable {
    data class Success<S>(val value: S) : ResultStatus<S, Nothing>
    data class Error<E>(val error: E) : ResultStatus<Nothing, E>
}

/**
 * Map on success.
 */
inline fun <T, TT, E> ResultStatus<T, E>.map(onSuccess: (T) -> TT): ResultStatus<TT, E> {
    return when (this) {
        is ResultStatus.Success -> ResultStatus.Success(onSuccess(this.value))
        is ResultStatus.Error -> this
    }
}

/**
 * Map on success, optionally map to a new error.
 */
inline fun <T, TT, E> ResultStatus<T, E>.flatMap(onSuccess: (T) -> ResultStatus<TT, E>): ResultStatus<TT, E> {
    return when (this) {
        is ResultStatus.Success -> onSuccess(this.value)
        is ResultStatus.Error -> this
    }
}

/**
 * Map on error.
 */
inline fun <T, E, EE> ResultStatus<T, E>.mapError(onError: (E) -> EE): ResultStatus<T, EE> {
    return when (this) {
        is ResultStatus.Success -> this
        is ResultStatus.Error -> ResultStatus.Error(onError(this.error))
    }
}

inline fun <T, E> ResultStatus<T, E>.alwaysDo(block: (ResultStatus<T, E>) -> Unit): ResultStatus<T, E> {
    block(this)
    return this
}

/**
 * Run the given block only on error.
 */
inline fun <T, E> ResultStatus<T, E>.doOnError(block: (errorType: E) -> Unit): ResultStatus<T, E> {
    when (this) {
        is ResultStatus.Success -> { /* no-op */ }
        is ResultStatus.Error -> block(this.error)
    }
    return this
}

/**
 * Run the given block only on success.
 */
inline fun <T, E> ResultStatus<T, E>.doOnSuccess(block: (value: T) -> Unit): ResultStatus<T, E> {
    when (this) {
        is ResultStatus.Success -> block(this.value)
        is ResultStatus.Error -> { /* no-op */ }
    }
    return this
}
