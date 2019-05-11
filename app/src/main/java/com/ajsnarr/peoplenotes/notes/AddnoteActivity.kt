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


        val toolbar = findViewById<View>(R.id.toolbar_addnote) as Toolbar
        setSupportActionBar(toolbar)

        val fab = findViewById<View>(R.id.fab_addnote_submit) as FloatingActionButton
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }

        // add to autocomplete text view
        val noteType = findViewById<View>(R.id.autocompletetext_addnote_notetype) as AutoCompleteTextView
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, NOTE_TYPES)
        noteType.setAdapter(adapter)

        // set up tags popup window
        val tagsBtn = findViewById<Button>(R.id.tags_popup_btn)
        EditNoteTagsPopup(this, R.id.layout_addnote_popupcontainer, tagsBtn)
    }

    companion object {
        val NOTE_TYPES = arrayOf("people", "location")
    }
}
