package com.ajsnarr.peoplenotes.notes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajsnarr.peoplenotes.BaseActivity

import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Entry
import com.ajsnarr.peoplenotes.data.Note
import com.ajsnarr.peoplenotes.data.NoteCollection
import com.ajsnarr.peoplenotes.util.getScreenSize
import kotlinx.android.synthetic.main.activity_editnote.*
import timber.log.Timber

val NOTE_TYPES = listOf("people", "location")

class EditNoteActivity : BaseActivity() {

    companion object {

        const val NOTE_INTENT_KEY = "note"

        fun getCreateNoteIntent(context: Context): Intent {
            return Intent(context, EditNoteActivity::class.java)
        }

        fun getEditNoteIntent(context: Context, note: Note): Intent {
            val intent = Intent(context, EditNoteActivity::class.java)
            intent.putExtra(NOTE_INTENT_KEY, note)
            return intent
        }
    }

    lateinit var viewModel: EditNoteViewModel

    private lateinit var mRecyclerAdapter: EntryAdapter
    private val mRecyclerActionListener = RecyclerActionListener(this)

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

        override fun onEditEntry(entry: Entry) {
            activity.viewModel.updateExistingEntry(entry)
        }

        override fun onSaveButtonPress() {
            val note = activity.viewModel.note

            // saved a valid note, refuse to save invalid note
            if (note.isValidNote()) {
                activity.mNotesCollection.add(note)
                Timber.i("Saved new note ${note.name}")

                val msg = if (note.isNewNote()) "Saved new note!" else "Saved note!"
                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activity, "Note needs to have a title", Toast.LENGTH_LONG).show()
            }
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

        val inNote: Note? = intent.getParcelableExtra(NOTE_INTENT_KEY)

        viewModel = ViewModelProviders.of(this, EditNoteViewModel.Factory(inNote)).get(EditNoteViewModel::class.java)

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
}
