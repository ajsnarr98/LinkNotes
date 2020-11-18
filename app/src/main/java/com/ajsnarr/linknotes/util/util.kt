package com.ajsnarr.linknotes.util

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import java.lang.IllegalArgumentException
import java.math.BigInteger


fun getScreenSize(activity: FragmentActivity) : Point {
    val size = Point()
    activity.windowManager.defaultDisplay.getSize(size) // size is an out parameter
    return size
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
