package com.ajsnarr.peoplenotes.notes

import android.content.Context
import android.graphics.Point
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.PopupWindow
import com.ajsnarr.peoplenotes.R

class AddnoteActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnote)


        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById<View>(R.id.fab_addnote_submit) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // add to autocomplete text view
        val noteType = findViewById<View>(R.id.autoCompleteNoteType_value) as AutoCompleteTextView
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, NOTE_TYPES)
        noteType.setAdapter(adapter)

        // set up tags popup window
        setupTagsPopup()
    }

    private fun setupTagsPopup() {
        val tagsBtn = findViewById<View>(R.id.tags_popup_btn) as Button

        val size = Point()
        this.windowManager.defaultDisplay.getSize(size) // size is an out parameter

        tagsBtn.setOnClickListener {
            //instantiate the layout file
            val layoutInflater = this@AddnoteActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val customView = layoutInflater.inflate(R.layout.popup_tags, null)

            val closePopupBtn = customView.findViewById<View>(R.id.closeTagsPopupBtn) as Button

            // instantiate popup window
            val popupWindow = PopupWindow(customView, (size.x * .80).toInt(), ViewGroup.LayoutParams.WRAP_CONTENT, true)

            //display the popup window
            val scrolling_layout = findViewById<View>(R.id.layout_addnote_popupcontainer)
            popupWindow.showAtLocation(scrolling_layout, Gravity.CENTER, 0, 0)

            //close the popup window on button click
            closePopupBtn.setOnClickListener { popupWindow.dismiss() }
        }
    }

    companion object {
        val NOTE_TYPES = arrayOf("people", "location")
    }
}
