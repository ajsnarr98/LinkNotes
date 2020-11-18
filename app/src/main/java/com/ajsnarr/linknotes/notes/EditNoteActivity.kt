package com.ajsnarr.linknotes.notes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajsnarr.linknotes.BaseActivity
import com.ajsnarr.linknotes.ConfirmationDialogFragment
import com.ajsnarr.linknotes.R
import com.ajsnarr.linknotes.data.Entry
import com.ajsnarr.linknotes.data.Note
import com.ajsnarr.linknotes.data.UUID
import com.ajsnarr.linknotes.databinding.ActivityEditnoteBinding
import com.ajsnarr.linknotes.search.SearchActivity
import com.ajsnarr.linknotes.util.getScreenSize
import com.ajsnarr.linknotes.util.hideKeyboard
import timber.log.Timber

open class EditNoteActivity : BaseActivity() {

    companion object {

        const val NOTE_INTENT_KEY = "note"

        fun getCreateNoteIntent(context: Context): Intent {
            return Intent(context, EditNoteActivity::class.java)
        }

        fun getEditNoteIntent(context: Context, note: Note): Intent {
            if (note.id != null && note.id.isNotEmpty()) {
                return getEditNoteIntent(context, note.id)
            } else {
                throw IllegalStateException("Note ID cannot be null or empty")
            }
        }

        fun getEditNoteIntent(context: Context, noteID: UUID): Intent {
            return Intent(context, EditNoteActivity::class.java).apply {
                putExtra(NOTE_INTENT_KEY, noteID)
            }
        }
    }

    lateinit var viewModel: EditNoteViewModel
    lateinit var binding: ActivityEditnoteBinding

    protected lateinit var recyclerAdapter: EditNoteAdapter
    private val mRecyclerActionListener = RecyclerActionListener(this)

    private class RecyclerActionListener(val activity: EditNoteActivity)
        : EditNoteAdapter.ActionListener {

        override fun onAddButtonPress() {
            activity.viewModel.addNewEntry()
            activity.recyclerAdapter.notifyDataSetChanged()

            // clear keyboard on add button press
            hideKeyboard(activity)
        }

        override fun onAddTag() {
            // set up tags popup window
            EditNoteTagsPopup(
                inflater = activity.layoutInflater,
                parentView = activity.binding.root,
                screenSize = getScreenSize(activity)
            )
        }

        override fun onDeletePress() {
            val note = activity.viewModel.note
            val onConfirmDelete: () -> Unit = {
                activity.mNotesCollection.remove(note)

                // clear backstack and go to search activity
                activity.intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                activity.startActivity(SearchActivity.getSearchIntent(activity))
            }

            val dialog = ConfirmationDialogFragment.newInstance(
                message = activity.getString(R.string.editnote_delete_note_confirmation, note.name),
                onConfirm = onConfirmDelete,
                onCancel = {}
            )
            dialog.show(activity.supportFragmentManager, "fragment_alert_note_delete")
        }

        override fun onDeleteEntryPress(entry: Entry) {

            val onConfirmDelete: () -> Unit = {
                activity.viewModel.deleteEntry(entry)
                activity.recyclerAdapter.notifyDataSetChanged()
            }

            val dialog = ConfirmationDialogFragment.newInstance(
                message = activity.getString(R.string.editnote_delete_entry_confirmation),
                onConfirm = onConfirmDelete,
                onCancel = {}
            )
            dialog.show(activity.supportFragmentManager, "fragment_alert_entry_delete")
        }

        override fun onEditEntry(entry: Entry): Entry? {
            return if (activity.viewModel.updateExistingEntry(entry))
                entry
            else
                null
        }

        override fun onSaveButtonPress() {

            // hide keyboard first
            hideKeyboard(activity)

            val note = activity.viewModel.note

            // save a valid note, refuse to save invalid note
            if (note.isValidNote()) {
                val msg = if (note.isNewNote()) "Saved new note!" else "Saved note!"
                note.fillDefaults()
                activity.mNotesCollection.add(note)
                Timber.i("${msg} ${note.name}")
                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()

                // navigate back
                activity.onBackPressed()
            } else {
                Toast.makeText(activity, "Note needs to have a title", Toast.LENGTH_LONG).show()
            }
        }

        override fun onSetNoteType(noteType: String) {
            activity.viewModel.noteType = noteType
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
        binding = ActivityEditnoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val inNoteID: UUID? = intent.getStringExtra(NOTE_INTENT_KEY)
        val inNote: Note? = mNotesCollection.findByID(inNoteID)

        viewModel = ViewModelProviders.of(this, EditNoteViewModel.Factory(inNote)).get(EditNoteViewModel::class.java)

        // set up close button
        binding.closeButton.setOnClickListener { onBackPressed() }

        // set up save button
        binding.saveButton.setOnClickListener { mRecyclerActionListener.onSaveButtonPress() }

        // set up recycler view
        val recyclerManager = LinearLayoutManager(this)
        recyclerAdapter = EditNoteAdapter(viewModel, mRecyclerActionListener)

        binding.recyclerView.apply {
            layoutManager = recyclerManager
            adapter = recyclerAdapter
        }
    }
}
