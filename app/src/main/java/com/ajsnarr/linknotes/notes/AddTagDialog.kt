package com.ajsnarr.linknotes.notes

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.ajsnarr.linknotes.BaseViewModel
import com.ajsnarr.linknotes.ConfirmationDialogFragment
import com.ajsnarr.linknotes.R
import com.ajsnarr.linknotes.data.Tag
import com.ajsnarr.linknotes.databinding.FragmentAddTagDialogBinding
import java.io.Serializable
import java.lang.IllegalStateException

/**
 * A dialogue fragment for scrolling through existing tags/creating a new one.
 *
 * @property viewModel the viewmodel to use for accessing the tag collection
 */
class AddTagDialog(private val viewModel: BaseViewModel) : DialogFragment() {

    companion object {

        private const val ON_POSITIVE_KEY = "onPositive"

        fun newInstance(viewModel: BaseViewModel, onConfirmListener: (tag: Tag) -> Unit) =
            AddTagDialog(viewModel).apply {
                arguments = Bundle().apply {
                    putSerializable(ON_POSITIVE_KEY, onConfirmListener as Serializable)
                }
            }
    }

    private lateinit var binding: FragmentAddTagDialogBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val activity = requireActivity()
        binding = FragmentAddTagDialogBinding.inflate(activity.layoutInflater)

        @Suppress("UNCHECKED_CAST")
        val onConfirmListener: (tag: Tag) -> Unit = arguments?.getSerializable(ON_POSITIVE_KEY) as? (tag: Tag) -> Unit
            ?: throw IllegalStateException("No confirm button callback found for AddTagDialog")

        // setup search bar view to allow handling of back button presses within
        // keyboard
        binding.textBarView.activity = activity

        binding.tagsGroup.addTags(viewModel.tagCollection) // add all tags

        return AlertDialog.Builder(activity).apply {
            setView(binding.root)
            setPositiveButton(R.string.generic_confirm) { dialog, _ ->
                val tagText = binding.textBar.text.toString()
                val tag = viewModel.tagCollection.getMatch(tagText)
                if (tag == null) {
                    // confirm when creating a completely new tag (in case of typo)
                    ConfirmationDialogFragment.newInstance(
                        onConfirm = {
                            val t = Tag(tagText)
                            viewModel.tagCollection.add(t)
                            onConfirmListener(t)
                            dialog?.dismiss()
                        },
                        onCancel = {},
                        message = context.getString(R.string.editnote_create_new_tag_confirmation, tagText),
                    ).show(parentFragmentManager, "confirm_tag_create_dialog")
                } else {
                    onConfirmListener(tag)
                    dialog?.dismiss()
                }
            }
            setNegativeButton(R.string.generic_cancel) { _, _ -> }
        }.create()
    }

}