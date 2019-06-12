package com.ajsnarr.peoplenotes.notes

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.MultiAutoCompleteTextView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Entry
import com.ajsnarr.peoplenotes.getScreenSize
import kotlinx.android.synthetic.main.activity_editnote.*

val NOTE_TYPES = listOf("people", "location")

class EditNoteActivity : AppCompatActivity() {

    lateinit var viewModel: EditNoteViewModel

    private lateinit var recyclerAdapter: EntryAdapter
    private val recyclerActionListener = RecyclerActionListener(this)

    private class RecyclerActionListener(val activity: EditNoteActivity)
        : EntryAdapter.ActionListener {

        override fun onAddButtonPress() {
            activity.viewModel.addEntry(Entry.newEmpty())
        }

        override fun onCreateTagsPopup() {
            // set up tags popup window
            EditNoteTagsPopup(
                inflater = activity.layoutInflater,
                parentView = activity.popupcontainer,
                screenSize = getScreenSize(activity)
            )
        }

        override fun onSetupNoteTypes(noteTypeField: AutoCompleteTextView) {
            // add to autocomplete text view
            val adapter = ArrayAdapter(activity,
                android.R.layout.simple_list_item_1, NOTE_TYPES
            )
            noteTypeField.setAdapter(adapter)
        }

        override fun onSetupNicknames(nicknameField: MultiAutoCompleteTextView) {
        }

        override fun onUpdateNumEntriesText(numEntriesText: TextView, numEntries: Int) {
            numEntriesText.text =
                activity.getString(R.string.editnote_num_entries, numEntries)
        }


    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editnote)

        viewModel = ViewModelProviders.of(this).get(EditNoteViewModel::class.java)

        // set up recycler view
        val recyclerManager = LinearLayoutManager(this)
        recyclerAdapter = EntryAdapter(viewModel.entries, recyclerActionListener)

        recycler_view.apply {
            layoutManager = recyclerManager
            adapter = recyclerAdapter
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
}