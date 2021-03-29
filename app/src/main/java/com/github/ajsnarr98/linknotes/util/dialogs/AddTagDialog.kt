package com.github.ajsnarr98.linknotes.util.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.github.ajsnarr98.linknotes.R
import com.github.ajsnarr98.linknotes.data.Note
import com.github.ajsnarr98.linknotes.data.Tag
import com.github.ajsnarr98.linknotes.databinding.FragmentAddTagDialogBinding
import java.io.Serializable
import java.lang.IllegalStateException

/**
 * A dialogue fragment for scrolling through existing tags/creating a new one.
 *
 * @property viewModel the viewmodel to use for accessing the tag collection
 */
class AddTagDialog(note: Note? = null) : DialogFragment() {

    companion object {

        private const val ON_POSITIVE_KEY = "onPositive"
        private const val NOTE_KEY = "note"

        fun newInstance(onConfirmListener: (tags: Set<Tag>) -> Unit) =
            newInstance(null, onConfirmListener)

        fun newInstance(note: Note?, onConfirmListener: (tags: Set<Tag>) -> Unit) =
            AddTagDialog().apply {
                arguments = Bundle().apply {
                    putSerializable(ON_POSITIVE_KEY, onConfirmListener as Serializable)
                    putParcelable(NOTE_KEY, note)
                }
            }
    }

    private lateinit var binding: FragmentAddTagDialogBinding
    private lateinit var viewModel: AddTagDialogViewModel

    private fun onEnterTag() {
        val tagText = binding.textBar.text.toString()
        val tag = viewModel.tagCollection.getMatch(tagText)
        if (tag == null) {
            // confirm when creating a completely new tag (in case of typo)
            ConfirmationDialogFragment.newInstance(
                onConfirm = {
                    val t = Tag(tagText)
                    viewModel.addTagToQueue(t, isNewTag = true)
                },
                onCancel = {},
                message = requireContext().getString(R.string.editnote_create_new_tag_confirmation, tagText),
            ).show(parentFragmentManager, "confirm_tag_create_dialog")
        } else {
            viewModel.addTagToQueue(tag)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        binding = FragmentAddTagDialogBinding.inflate(activity.layoutInflater)
        viewModel = ViewModelProvider(this).get(AddTagDialogViewModel::class.java)

        @Suppress("UNCHECKED_CAST")
        val onConfirmListener: (tags: Set<Tag>) -> Unit = arguments?.getSerializable(ON_POSITIVE_KEY) as? (tags: Set<Tag>) -> Unit
            ?: throw IllegalStateException("No confirm button callback found for AddTagDialog")

        // setup search bar view to allow handling of back button presses within
        // keyboard
        binding.textBarView.activity = activity

        binding.tagsGroup.addTags(viewModel.tagCollection) // add all tags
        binding.tagsGroup.setOnTagClickListener { tag -> viewModel.addTagToQueue(tag) }

        binding.tagsToAdd.setOnTagClickListener { tag -> viewModel.removeTagFromQueue(tag) }
        binding.tagsToAdd.visibility = View.GONE

        binding.addButton.setOnClickListener { onEnterTag() }

        binding.textBar.setOnEditorActionListener { v, actionId, event ->
            return@setOnEditorActionListener when(actionId) {
                EditorInfo.IME_ACTION_SEND -> {
                    onEnterTag()
                    true
                }
                else -> false
            }
        }

        val visibility = if (viewModel.tagCollection.isEmpty()) View.GONE else View.VISIBLE
        binding.divider.visibility = visibility
        binding.tagsGroup.visibility = visibility

        viewModel.tagQueueLiveData.observe(this, { tags ->
            binding.tagsToAdd.setTags(tags)
            binding.tagsToAdd.visibility = if (tags.isEmpty()) View.GONE else View.VISIBLE
            binding.divider.visibility = when {
                tags.isEmpty() -> View.GONE
                binding.tagsGroup.tags.isEmpty() -> View.GONE
                else -> View.VISIBLE
            }
        })

        viewModel.selectableTagsLiveData.observe(this, { tags ->
            binding.tagsGroup.setTags(tags)
            binding.tagsGroup.visibility = if (tags.isEmpty()) View.GONE else View.VISIBLE
            binding.divider.visibility = when {
                tags.isEmpty() -> View.GONE
                binding.tagsToAdd.tags.isEmpty() -> View.GONE
                else -> View.VISIBLE
            }
        })

        // add in all existing tags for this note, if a valid note was given
        val note: Note? = arguments?.getParcelable(NOTE_KEY)
        if (note != null) {
            viewModel.addTagsToQueue(note.tags)
        }

        return AlertDialog.Builder(activity).apply {
            setView(binding.root)
            setPositiveButton(R.string.generic_confirm) { dialog, _ ->
                if (viewModel.tagQueue.isNotEmpty()) onConfirmListener(viewModel.tagQueue)
                viewModel.onConfirmDialog()
                dialog?.dismiss()
            }
            setNegativeButton(R.string.generic_cancel) { _, _ -> }
            retainInstance = true
        }.create()
    }

}