package com.github.ajsnarr98.linknotes.notes

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajsnarr98.linknotes.BaseActivity
import com.github.ajsnarr98.linknotes.R
import com.github.ajsnarr98.linknotes.util.dialogs.ConfirmationDialogFragment
import com.github.ajsnarr98.linknotes.data.Entry
import com.github.ajsnarr98.linknotes.data.Note
import com.github.ajsnarr98.linknotes.data.Tag
import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.databinding.ActivityEditnoteBinding
import com.github.ajsnarr98.linknotes.search.SearchActivity
import com.github.ajsnarr98.linknotes.util.dialogs.AddTagDialog
import com.github.ajsnarr98.linknotes.util.hideKeyboard
import timber.log.Timber

open class EditNoteActivity : BaseActivity() {

    companion object {

        const val NOTE_INTENT_KEY = "note"
        const val UNSAVED_CHANGES_INTENT_KEY = "has_unsaved_changes"

        fun getCreateNoteIntent(context: Context): Intent {
            return Intent(context, EditNoteActivity::class.java)
        }

        fun getUnsavedChangesIntent(context: Context): Intent {
            return Intent(context, EditNoteActivity::class.java).apply {
                putExtra(UNSAVED_CHANGES_INTENT_KEY, true)
            }
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
                putExtra(UNSAVED_CHANGES_INTENT_KEY, false)
            }
        }
    }

    lateinit var viewModel: EditNoteViewModel
    private lateinit var binding: ActivityEditnoteBinding
    override val rootView: ViewGroup get() = binding.root

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

        override fun onAddTags() {
            AddTagDialog.newInstance(activity.viewModel.note) { tags ->
                // this is called when tags are going to be changed in this note
                activity.viewModel.setTags(tags)
                activity.recyclerAdapter.notifyDataSetChanged()
            }.show(activity.supportFragmentManager, "add_tags_dialog")
        }

        override fun onDeletePress() {
            val note = activity.viewModel.note
            val onConfirmDelete: () -> Unit = {
                activity.viewModel.notesCollection.remove(note)

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

        override fun onRemoveTag(tag: Tag) {
            ConfirmationDialogFragment.newInstance(
                message = activity.getString(R.string.editnote_remove_tag_confirmation, tag.text),
                onConfirm = {
                    activity.viewModel.removeTag(tag)
                    activity.recyclerAdapter.notifyDataSetChanged()
                },
                onCancel = {},
            ).show(activity.supportFragmentManager, "confirm_remove_tag")
        }

        override fun onSaveButtonPress() {

            // hide keyboard first
            hideKeyboard(activity)

            val note = activity.viewModel.note

            // save a valid note, refuse to save invalid note
            if (note.isValidNote()) {
                val msg = if (note.isNewNote()) "Saved new note!" else "Saved note!"
                activity.viewModel.saveNote()
                Timber.i("${msg} ${note.name}")
                Toast.makeText(activity, msg, Toast.LENGTH_LONG).show()

                // navigate back
                activity.onBackPressed(skipConfirmation = true)
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
        val hasUnsavedChanges: Boolean = intent.getBooleanExtra(UNSAVED_CHANGES_INTENT_KEY, false)

        viewModel = ViewModelProvider(
            this, EditNoteViewModel.Factory(inNoteID, hasUnsavedChanges)
        ).get(EditNoteViewModel::class.java)
        viewModel.lifecycleObservers.forEach { lifecycle.addObserver(it) }

        // set up close button
        binding.closeButton.setOnClickListener { onBackPressed(skipConfirmation = false) }

        // set up save button
        binding.saveButton.setOnClickListener { mRecyclerActionListener.onSaveButtonPress() }

        // set up undo button
        binding.undoButton.setOnClickListener {
            viewModel.undo()
            recyclerAdapter.notifyDataSetChanged()
        }
        viewModel.canUndo.observe(this) { canUndo ->
            binding.undoButton.isEnabled = canUndo
        }
        binding.undoButton.isEnabled = false

        // set up redo button
        binding.redoButton.setOnClickListener {
            viewModel.redo()
            recyclerAdapter.notifyDataSetChanged()
        }
        viewModel.canRedo.observe(this) { canRedo ->
            binding.redoButton.isEnabled = canRedo
        }
        binding.redoButton.isEnabled = false

        // set up recycler view
        val recyclerManager = LinearLayoutManager(this)
        recyclerAdapter = EditNoteAdapter(viewModel, mRecyclerActionListener)

        binding.recyclerView.apply {
            layoutManager = recyclerManager
            adapter = recyclerAdapter
        }
    }

    override fun onBackPressed() {
        onBackPressed(skipConfirmation = !viewModel.hasMadeChanges)
    }

    /**
     * Calls onBackPressed, but presents a confirmation dialog if you made any
     * changes and skipConfirmation is false.
     */
    private fun onBackPressed(skipConfirmation: Boolean) {
        if (skipConfirmation || !viewModel.hasMadeChanges) {
            viewModel.onCancel()
            super.onBackPressed()
        } else {
            ConfirmationDialogFragment.newInstance(
                message = this.getString(R.string.editnote_cancel_edit_confirmation),
                confirmButtonMessage = this.getString(R.string.discard),
                onConfirm = { viewModel.onCancel(); super.onBackPressed() },
                onCancel = {},
            ).show(supportFragmentManager, "fragment_alert_cancel_confirmation")
        }
    }
}
