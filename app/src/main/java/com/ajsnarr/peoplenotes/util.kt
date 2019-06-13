package com.ajsnarr.peoplenotes

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentActivity
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.getSystemService





fun getScreenSize(activity: FragmentActivity) : Point {
    val size = Point()
    activity.windowManager.defaultDisplay.getSize(size) // size is an out parameter
    return size
}

/**
 * Hides the keyboard. Why does this have to be a thing!
 */
fun hideKeyboard(activity: Activity) {
    var view = activity.currentFocus
    if (view == null) {
        view = View(activity)
    }
    hideKeyboardFrom(activity, view)
}

/**
 * Hides the keyboard. Why does this have to be a thing!
 */
fun hideKeyboardFrom(context: Context, view: View) {
    val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}
