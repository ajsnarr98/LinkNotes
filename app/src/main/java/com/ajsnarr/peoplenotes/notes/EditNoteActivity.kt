package com.ajsnarr.peoplenotes.notes

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager

import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Entry
import com.ajsnarr.peoplenotes.data.Note
import com.ajsnarr.peoplenotes.db.NoteCollection
import com.ajsnarr.peoplenotes.util.getScreenSize
import kotlinx.android.synthetic.main.activity_editnote.*
import timber.log.Timber

val NOTE_TYPES = listOf("people", "location")

class EditNoteActivity : AppCompatActivity() {

    lateinit var viewModel: EditNoteViewModel

    private lateinit var mRecyclerAdapter: EntryAdapter
    private val mRecyclerActionListener = RecyclerActionListener(this)

    private val mDbNotesCollection = NoteCollection.instance

    private class RecyclerActionListener(val activity: EditNoteActivity)
        : EntryAdapter.ActionListener {

        override fun onAddButtonPress() {
            activity.viewModel.addNewEntry()
        }

        override fun onAddTag() {
            // set up tags popup window
            EditNoteTagsPopup(
                inflater = activity.layoutInflater,
                parentView = activity.popupcontainer,
                screenSize = getScreenSize(activity)
            )
        }

        override fun onSaveButtonPress() {
            Timber.i("Saved note ${activity.viewModel.note.name}")
            Timber.d("note: ${activity.viewModel.note}")
            activity.mDbNotesCollection.add(activity.viewModel.note.toDBObject())
            Toast.makeText(activity, "Saved note!", Toast.LENGTH_LONG).show()
        }

        override fun onSetTitle(title: String) {
            activity.viewModel.title = title
        }

        override fun onSetupNoteTypes(noteTypeField: AutoCompleteTextView) {
            // add to autocomplete text view
            val adapter = ArrayAdapter(activity,
                android.R.layout.simple_list_item_1, NOTE_TYPES
            )
            noteTypeField.setAdapter(adapter)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editnote)

        viewModel = ViewModelProviders.of(this, EditNoteViewModel.Factory(null)).get(EditNoteViewModel::class.java)

        // set up recycler view
        val recyclerManager = LinearLayoutManager(this)
        mRecyclerAdapter = EntryAdapter(viewModel.note, mRecyclerActionListener)

        recycler_view.apply {
            layoutManager = recyclerManager
            adapter = mRecyclerAdapter
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

    override fun onStart() {
        super.onStart()
        mDbNotesCollection.onActivityStart()
    }

    override fun onStop() {
        super.onStop()
        mDbNotesCollection.onActivityStop()
    }
}