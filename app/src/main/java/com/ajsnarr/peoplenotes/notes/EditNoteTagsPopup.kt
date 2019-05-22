package com.ajsnarr.peoplenotes.notes

import android.content.Context
import android.graphics.Point
import android.text.Layout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.getScreenSize
import java.util.zip.Inflater
import kotlin.properties.Delegates

/**
 * EditNoteTagsPopup represents a popup window for adding tags, placed in the current activity.
 *
 * @param parentActivity Activity this popup window will appear in.
 * @param parentViewId View within the parent activity that this popup will appear in
 * @param parentButton (Optional) Clickable view that will pull up this button
 */
class EditNoteTagsPopup(val inflater: LayoutInflater,
                        val parentView: View,
                        val screenSize: Point,
                        val parentButton: View? = null) {

    lateinit var popupWindow: PopupWindow

    init {
        if (parentButton == null)
            createPopupWindow()
        else
            parentButton.setOnClickListener { createPopupWindow() }
    }

    fun dismiss() = popupWindow.dismiss()

    private fun createPopupWindow() {

        val customView = inflater.inflate(R.layout.view_editnote_tagspopup, null)
        val closePopupBtn = customView.findViewById<View>(R.id.closeTagsPopupBtn) as Button

        // instantiate popup window
        popupWindow = PopupWindow(customView, (screenSize.x * .80).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT, true)

        //display the popup window
        popupWindow.showAtLocation(parentView, Gravity.CENTER, 0, 0)

        //close the popup window on button click
        closePopupBtn.setOnClickListener { popupWindow.dismiss() }

    }
}