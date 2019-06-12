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
import kotlinx.android.synthetic.main.activity_addnote.*

val NOTE_TYPES = listOf("people", "location")

val entries = mutableListOf<Entry>(
    Entry("12345"),
    Entry("123456"))

class AddnoteActivity : AppCompatActivity() {

    lateinit var recyclerAdapter: EntryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addnote)

        // add to autocomplete text view
        val noteType = findViewById<AutoCompleteTextView>(R.id.notetype_auto_input)
        val adapter = ArrayAdapter(this,
            android.R.layout.simple_list_item_1, NOTE_TYPES
        )
        noteType.setAdapter(adapter)

        // set up tags popup window
        EditNoteTagsPopup(layoutInflater, popupcontainer, getScreenSize(this), tags_popup_btn)

        // number of entries text
        updateNumEntriesText()

        // set up recycler view
        val recyclerManager = LinearLayoutManager(this)
        recyclerAdapter = EntryAdapter(entries)

        recycler_view.apply {
            layoutManager = recyclerManager
            setAdapter(recyclerAdapter)
        }

        // set up add entry button
        add_entry_button.setOnClickListener {
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
        num_entries_text.text = getString(R.string.editnote_num_entries, entries.size)
    }
}