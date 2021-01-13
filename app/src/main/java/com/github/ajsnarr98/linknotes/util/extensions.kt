package com.github.ajsnarr98.linknotes.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

/**
 * Checks if this object is not null
 */
@ExperimentalContracts
fun Any?.isNotNull(): Boolean {
    contract {
        returns(true) implies (this@isNotNull != null)
    }
    return this != null
}

/**
 * Checks if this string is not null and not full of empty spaces.
 */
@ExperimentalContracts
fun String?.isNotNullOrBlank(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrBlank != null)
    }
    return this != null && this.isNotBlank()
}

/**
 * Checks if this string is not null and not empty string.
 */
@ExperimentalContracts
fun String?.isNotNullOrEmpty(): Boolean {
    contract {
        returns(true) implies (this@isNotNullOrEmpty != null)
    }
    return this != null && this.isNotEmpty()
}
