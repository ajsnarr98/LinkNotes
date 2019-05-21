package com.ajsnarr.peoplenotes

import android.graphics.Point
import androidx.fragment.app.FragmentActivity

fun getScreenSize(activity: FragmentActivity) : Point {
    val size = Point()
    activity.windowManager.defaultDisplay.getSize(size) // size is an out parameter
    return size
}