package com.github.ajsnarr98.linknotes.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajsnarr.linknotes.R
import com.github.ajsnarr98.linknotes.data.Entry
import com.ajsnarr.linknotes.databinding.ItemViewnoteDetailsBinding
import com.ajsnarr.linknotes.databinding.ItemViewnoteEntryBinding
import io.noties.markwon.Markwon
import timber.log.Timber
import java.lang.IllegalArgumentException

class ViewNoteAdapter(private val viewModel: ViewNoteViewModel,
                      private val actionListener: ActionListener,
                      private val markwon: Markwon,
) : RecyclerView.Adapter<ViewNoteAdapter.ViewHolder>() {

    companion object {
        const val ENTRY_TYPE = 0
        const val NOTE_DETAILS_TYPE = 1
    }

    /**
     * Pass this into an instance of ViewNoteAdapter to subscribe to events.
     */
    interface ActionListener {
        /**
         * Called when the edit button is pressed.
         */
        fun onEditButtonPress()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return when (viewType) {
            ENTRY_TYPE -> EntryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_viewnote_entry, parent, false),
                viewModel, actionListener, markwon
            )
            NOTE_DETAILS_TYPE -> {
                NoteDetailViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_viewnote_details, parent, false),
                    viewModel, actionListener, markwon
                )
            }
            else -> throw IllegalArgumentException("Unhandled viewType: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> NOTE_DETAILS_TYPE
            else -> ENTRY_TYPE
        }
    }

    override fun getItemCount(): Int = viewModel.note.entries.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return when (holder) {
            is EntryViewHolder -> holder.onBind(viewModel.note.entries[position - 1])
            is NoteDetailViewHolder -> holder.onBind()
            else -> throw IllegalArgumentException("Unhandled viewType: ${holder::class.qualifiedName}")
        }
    }

    abstract class ViewHolder(
        protected val view: View,
        protected val viewModel: ViewNoteViewModel,
        protected val actionListener: ActionListener,
        protected val markwon: Markwon,
    ) : RecyclerView.ViewHolder(view)

    class EntryViewHolder(view: View, viewModel: ViewNoteViewModel, actionListener: ActionListener, markwon: Markwon) :
        ViewHolder(view, viewModel, actionListener, markwon) {

        private lateinit var binding: ItemViewnoteEntryBinding

        fun onBind(entry: Entry) {
            Timber.d("onBindEntry")
            binding = ItemViewnoteEntryBinding.bind(view)

            binding.entryType.text = entry.type.value
            binding.content.text = markwon.toMarkdown(entry.content.value)
        }
    }

    class NoteDetailViewHolder(view: View, viewModel: ViewNoteViewModel, actionListener: ActionListener, markwon: Markwon) :
        ViewHolder(view, viewModel, actionListener, markwon) {

        private lateinit var binding: ItemViewnoteDetailsBinding

        fun onBind() {

            if (viewModel.note.isNewNote()) throw IllegalStateException("Cannot view empty note.")

            Timber.d("onBindNoteDetails")
            binding = ItemViewnoteDetailsBinding.bind(view)

            // setup tags
            if (viewModel.note.tags.isEmpty()) {
                binding.tagChipGroup.visibility = View.GONE
            } else {
                binding.tagChipGroup.visibility = View.VISIBLE
                binding.tagChipGroup.setTags(viewModel.note.tags)
            }

            // update num entries text
            updateNumEntriesText()

            binding.title.text = viewModel.note.name
            binding.noteType.text = viewModel.note.type

            // leave nicknames field blank if there are no nicknames
            if (viewModel.note.nicknames.size > 0) {
                val nickString: String = viewModel.note.nicknames.joinToString(separator = ", ")
                binding.nicknames.text = nickString
            } else {
                binding.nicknamesTitle.visibility = View.INVISIBLE
                binding.nicknames.visibility = View.INVISIBLE
            }

            // add listener for edit
            binding.editButton.setOnClickListener {
                actionListener.onEditButtonPress()
            }

            // TODO image
        }

        fun updateNumEntriesText() {
            val numEntries: Int = viewModel.note.entries.size

            binding.numEntriesText.text =
                view.context.getString(R.string.editnote_num_entries, numEntries)
        }

    }
}