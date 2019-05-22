package com.ajsnarr.peoplenotes.notes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.fragment.app.Fragment

import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.getScreenSize

val NOTE_TYPES = listOf("people", "location")

class AddnoteFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_addnote, container, false)

        // add to autocomplete text view
        val noteType = view.findViewById<AutoCompleteTextView>(R.id.autocompletetext_addnote_notetype)
        val adapter = ArrayAdapter(activity!!,
            android.R.layout.simple_list_item_1, NOTE_TYPES
        )
        noteType.setAdapter(adapter)

        // set up tags popup window
        val tagsBtn = view.findViewById<Button>(R.id.tags_popup_btn)
        val containingView = view.findViewById<View>(R.id.layout_addnote_popupcontainer)
        EditNoteTagsPopup(activity!!.layoutInflater, containingView, getScreenSize(activity!!), tagsBtn)

        return view
    }
}