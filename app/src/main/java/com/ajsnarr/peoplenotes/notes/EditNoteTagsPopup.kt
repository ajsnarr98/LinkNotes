package com.ajsnarr.peoplenotes.notes

import android.content.Context
import android.graphics.Point
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import com.ajsnarr.peoplenotes.R
import kotlin.properties.Delegates

/**
 * EditNoteTagsPopup represents a popup window for adding tags, placed in the current activity.
 *
 * @param parentActivity Activity this popup window will appear in.
 * @param parentViewId View within the parent activity that this popup will appear in
 * @param parentButton (Optional) Clickable view that will pull up this button
 */
class EditNoteTagsPopup(val parentActivity: AppCompatActivity, val parentViewId: Int, val parentButton: View? = null) {
    var popupWindow: PopupWindow by Delegates.notNull()

    init {
        if (parentButton == null)
            createPopupWindow()
        else
            parentButton.setOnClickListener { createPopupWindow() }
    }

    fun dismiss() = popupWindow.dismiss()

    private fun createPopupWindow() {
        val size = getScreenSize()
        val layoutInflater = getLayoutInflater()

        val customView = layoutInflater.inflate(R.layout.view_editnote_tagspopup, null)
        val closePopupBtn = customView.findViewById<View>(R.id.closeTagsPopupBtn) as Button

        // instantiate popup window
        popupWindow = PopupWindow(customView, (size.x * .80).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT, true)

        //display the popup window
        val parent_view = parentActivity.findViewById<View>(parentViewId)
        popupWindow.showAtLocation(parent_view, Gravity.CENTER, 0, 0)

        //close the popup window on button click
        closePopupBtn.setOnClickListener { popupWindow.dismiss() }

    }

    private fun getLayoutInflater(): LayoutInflater = parentActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    private fun getScreenSize(): Point {
        val size = Point()
        parentActivity.windowManager.defaultDisplay.getSize(size) // size is an out parameter
        return size
    }
}