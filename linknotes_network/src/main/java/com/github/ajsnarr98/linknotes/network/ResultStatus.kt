package com.github.ajsnarr98.linknotes.network

sealed interface ResultStatus<T> : java.io.Serializable {

    val result: T

    /**
     * Result for when successfully completed action.
     */
    data class Success<T>(override val result: T) : ResultStatus<T>

    /**
     * Result for when need to wait for network connection before trying again.
     */
    data class Error<T>(override val result: T, val error: ErrorType) : ResultStatus<T>

    sealed interface ErrorType {
        object NoNetwork : ErrorType
        sealed interface LocalWarning {
            object PendingChangesNotResolved : ErrorType, LocalWarning
            object TagUsedInNote : ErrorType, LocalWarning
            object NoSavedToken : ErrorType, LocalWarning
            object NoSavedServiceId : ErrorType, LocalWarning
            object InvalidToken : ErrorType, LocalWarning
        }
        data class Other(val code: Int) : ErrorType
    }
}