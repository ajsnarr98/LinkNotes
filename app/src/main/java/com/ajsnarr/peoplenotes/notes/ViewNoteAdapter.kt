package com.ajsnarr.peoplenotes.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Entry
import com.ajsnarr.peoplenotes.data.Note
import com.ajsnarr.peoplenotes.databinding.ItemViewnoteDetailsBinding
import com.ajsnarr.peoplenotes.databinding.ItemViewnoteEntryBinding
import java.lang.IllegalArgumentException

class ViewNoteAdapter(private val note: Note,
                      private val actionListener: ActionListener
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
                this, actionListener
            )
            NOTE_DETAILS_TYPE -> {
                NoteDetailViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_viewnote_details, parent, false),
                    this, actionListener
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

    override fun getItemCount(): Int = note.entries.size + 1

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return when (holder) {
            is EntryViewHolder -> holder.onBind(note.entries[position - 1])
            is NoteDetailViewHolder -> holder.onBind()
            else -> throw IllegalArgumentException("Unhandled viewType: ${holder::class.qualifiedName}")
        }
    }

    abstract class ViewHolder(
        protected val view: View,
        protected val adapter: ViewNoteAdapter,
        protected val actionListener: ActionListener
    ) : RecyclerView.ViewHolder(view)

    class EntryViewHolder(view: View, adapter: ViewNoteAdapter, actionListener: ActionListener) :
        ViewHolder(view, adapter, actionListener) {

        val binding = ItemViewnoteEntryBinding.bind(view)

        fun onBind(entry: Entry) {
            binding.entryType.text = entry.type.value
            binding.content.text = entry.content.value
        }
    }

    class NoteDetailViewHolder(view: View, adapter: ViewNoteAdapter, actionListener: ActionListener) :
        ViewHolder(view, adapter, actionListener) {

        private val binding = ItemViewnoteDetailsBinding.bind(view)

        fun onBind() {

            if (adapter.note.isNewNote()) throw IllegalStateException("Cannot view empty note.")

            // update num entries text
            updateNumEntriesText()

            binding.title.text = adapter.note.name
            binding.noteType.text = adapter.note.type

            // leave nicknames field blank if there are no nicknames
            if (adapter.note.nicknames.size > 0) {
                val nickString: String = adapter.note.nicknames.joinToString(separator = "\n")
                binding.nicknames.text =
                    view.context.getString(R.string.viewnote_nicknames_text, nickString)
            } else {
                binding.nicknames.text = ""
            }

            // add listener for edit
            binding.editButton.setOnClickListener {
                actionListener.onEditButtonPress()
            }

            // TODO image
        }

        fun updateNumEntriesText() {
            val numEntries: Int = adapter.note.entries.size

            binding.numEntriesText.text =
                view.context.getString(R.string.editnote_num_entries, numEntries)
        }

    }
}