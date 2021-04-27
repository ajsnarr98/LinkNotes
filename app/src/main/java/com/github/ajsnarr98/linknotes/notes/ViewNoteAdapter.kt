package com.github.ajsnarr98.linknotes.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.ajsnarr98.linknotes.R
import com.github.ajsnarr98.linknotes.data.Entry
import com.github.ajsnarr98.linknotes.data.EntryType
import com.github.ajsnarr98.linknotes.databinding.ItemViewnoteDetailsBinding
import com.github.ajsnarr98.linknotes.databinding.ItemViewnoteEntryBinding
import io.noties.markwon.Markwon
import timber.log.Timber
import java.lang.IllegalArgumentException

class ViewNoteAdapter(private val viewModel: ViewNoteViewModel,
                      private val actionListener: ActionListener,
                      private val markwon: Markwon,
) : RecyclerView.Adapter<ViewNoteAdapter.ViewHolder>() {

    companion object {
        const val IMAGE_ENTRY_TYPE = -1
        const val DEFAULT_ENTRY_TYPE = 0
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
            DEFAULT_ENTRY_TYPE -> EntryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_viewnote_entry, parent, false),
                markwon
            )
            IMAGE_ENTRY_TYPE -> SpecialEntryViewHolder(
                ImageEntryView(
                    context = parent.context,
                    isEditable = false,
                )
            )
            NOTE_DETAILS_TYPE -> {
                NoteDetailViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_viewnote_details, parent, false),
                    viewModel, actionListener
                )
            }
            else -> throw IllegalArgumentException("Unhandled viewType: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> NOTE_DETAILS_TYPE
            else -> when (viewModel.note.entries[position - 1].type) {
                is EntryType.IMAGES -> IMAGE_ENTRY_TYPE
                is EntryType.DEFAULT, is EntryType.CUSTOM -> DEFAULT_ENTRY_TYPE
            }
        }
    }

    override fun getItemCount(): Int = viewModel.note.entries.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return when (holder) {
            is EntryViewHolder -> holder.onBind(viewModel.note.entries[position - 1])
            is SpecialEntryViewHolder -> holder.onBind(viewModel.note.entries[position - 1])
            is NoteDetailViewHolder -> holder.onBind()
            else -> throw IllegalArgumentException("Unhandled viewType: ${holder::class.qualifiedName}")
        }
    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    /** Holds a special type of entry view that has implemented EntryView. */
    class SpecialEntryViewHolder(private val entryView: EntryView) : ViewHolder(entryView.view) {
        fun onBind(entry: Entry) {
            entryView.bind(entry)
        }
    }

    class EntryViewHolder(
            view: View,
            private val markwon: Markwon,
        ) : ViewHolder(view) {

        private val binding = ItemViewnoteEntryBinding.bind(itemView)

        fun onBind(entry: Entry) {
            Timber.d("onBindEntry")
            binding.entryType.text = entry.type.value
            binding.content.text = markwon.toMarkdown(entry.content.value)
        }
    }

    class NoteDetailViewHolder(
        view: View,
        private val viewModel: ViewNoteViewModel,
        private val actionListener: ActionListener,
    ) : ViewHolder(view) {

        private val binding = ItemViewnoteDetailsBinding.bind(itemView)

        fun onBind() {
            if (viewModel.note.isNewNote()) throw IllegalStateException("Cannot view empty note.")

            Timber.d("onBindNoteDetails")

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
                itemView.context.getString(R.string.editnote_num_entries, numEntries)
        }

    }
}