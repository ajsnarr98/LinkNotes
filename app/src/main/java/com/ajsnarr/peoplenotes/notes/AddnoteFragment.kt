package com.ajsnarr.peoplenotes.notes

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Entry
import com.ajsnarr.peoplenotes.getScreenSize

val NOTE_TYPES = listOf("people", "location")

val entries = mutableListOf<Entry>(
    Entry("12345"),
    Entry("123456"))

class AddnoteFragment : Fragment() {

    lateinit var recyclerView: RecyclerView

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

        // number of entries text
        val numEntriesText = view.findViewById<TextView>(R.id.text_addnote_numentries)
        numEntriesText.text = getString(R.string.editnote_num_entries, entries.size)


        // set up recycler view
        val recyclerManager = LinearLayoutManager(activity)
        val recyclerAdapter = EntryAdapter(entries)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_addnote_entries).apply {
            layoutManager = recyclerManager
            setAdapter(recyclerAdapter)
        }


        return view
    }
}