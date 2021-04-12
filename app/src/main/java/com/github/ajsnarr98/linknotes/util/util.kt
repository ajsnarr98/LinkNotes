package com.github.ajsnarr98.linknotes.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.graphics.Point
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import io.noties.markwon.Markwon
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.lang.IllegalArgumentException
import java.math.BigInteger

fun createMarkwonInstance(context: Context): Markwon = Markwon.builder(context)
    .build()

fun fuzzyMatch(searchStr: String, matchTo: String): Boolean {
    return if (searchStr.length <= matchTo.length) {
        // match for part of given strs if search string is less than string to be matched to
        FuzzySearch.partialRatio(searchStr, matchTo) >= 70
    } else {
        // make search more condensed as search string gets bigger
        FuzzySearch.ratio(searchStr, matchTo) >= 85
    }
}

/**
 * Tries to get the activity from given context. This may fail.
 *
 * @return the parent activity or null
 */
fun getActivity(context: Context?): Activity? {
    return when (context) {
        is Activity -> context
        is ContextWrapper -> getActivity(context.baseContext)
        else -> null
    }
}

/**
 * Hides the keyboard
 */
fun hideKeyboard(activity: Activity) {
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    hideKeyboardFrom(activity, view)
}

/**
 * Hides the keyboard
 */
fun hideKeyboardFrom(context: Context, view: View) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

/**
 * Returns max value given.
 */
fun max(vararg nums: BigInteger): BigInteger {
    if (nums.size == 0) { throw IllegalArgumentException("Cannot get max value of empty list") }

    var max = nums[0]
    for (i in 1..nums.size-1) {
        max = max.max(nums[i])
    }
    return max
}

/**
 * Returns the max value given.
 */
fun max(vararg nums: Int): Int {
    if (nums.size == 0) { throw IllegalArgumentException("Cannot get max value of empty list") }

    var max = nums[0]
    for (i in 1..nums.size-1) {
        if (nums[i] > max) {
            max = nums[i]
        }
    }
    return max
}

/**
 * Returns min value given.
 */
fun min(vararg nums: BigInteger): BigInteger {
    if (nums.size == 0) { throw IllegalArgumentException("Cannot get max value of empty list") }

    var min = nums[0]
    for (i in 1..nums.size-1) {
        min = min.min(nums[i])
    }
    return min
}

/**
 * Returns the min value given.
 */
fun min(vararg nums: Int): Int {
    if (nums.size == 0) { throw IllegalArgumentException("Cannot get max value of empty list") }

    var min = nums[0]
    for (i in 1..nums.size-1) {
        if (nums[i] < min) {
            min = nums[i]
        }
    }
    return min
}
