package com.github.ajsnarr98.linknotes.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.github.ajsnarr98.linknotes.BaseActivity
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

/**
 * Attempts to unwrap the current activity from the given context, or will
 * return null if the activity cannot be found. Avoid using this method
 * if possible.
 *
 * This will always return null when given application context.
 */
fun Context?.getActivityOrNull(): BaseActivity? {
    return if (this is ContextWrapper) {
        if (this is BaseActivity)
            this
        else
            this.baseContext.getActivityOrNull()
    } else {
        null
    }
}
