package com.ajsnarr.peoplenotes.notes

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Entry
import com.ajsnarr.peoplenotes.getScreenSize

val NOTE_TYPES = listOf("people", "location")

val entries = mutableListOf<Entry>(
    Entry("12345"),
    Entry("123456"))

class AddnoteActivity : AppCompatActivity() {

    lateinit var recyclerView: RecyclerView
    lateinit var recyclerAdapter: EntryAdapter

    lateinit var numEntriesText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnote)

        // add to autocomplete text view
        val noteType = findViewById<AutoCompleteTextView>(R.id.autocompletetext_addnote_notetype)
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, NOTE_TYPES
        )
        noteType.setAdapter(adapter)

        // set up tags popup window
        val tagsBtn = findViewById<Button>(R.id.tags_popup_btn)
        val containingView = findViewById<View>(R.id.layout_addnote_popupcontainer)
        EditNoteTagsPopup(layoutInflater, containingView, getScreenSize(this), tagsBtn)

        // number of entries text
        numEntriesText = findViewById(R.id.text_addnote_numentries)
        updateNumEntriesText()


        // set up recycler view
        val recyclerManager = LinearLayoutManager(this)
        recyclerAdapter = EntryAdapter(entries)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_addnote_entries).apply {
            layoutManager = recyclerManager
            setAdapter(recyclerAdapter)
        }

        // set up add entry button
        val addEntryBtn = findViewById<Button>(R.id.button_editnote_addentry)
        addEntryBtn.setOnClickListener {
            entries.add(Entry.newEmpty())
            recyclerAdapter.notifyDataSetChanged()
            updateNumEntriesText()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_settings -> return true // TODO
        }
        return super.onOptionsItemSelected(item)
    }

    fun updateNumEntriesText() {
        numEntriesText.text = getString(R.string.editnote_num_entries, entries.size)
    }
}