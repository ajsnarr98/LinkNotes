package com.ajsnarr.peoplenotes.notes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Entry
import com.ajsnarr.peoplenotes.data.Note
import java.lang.IllegalArgumentException

class ViewNoteAdapter(private val note: Note,
                      private val actionListener: ActionListener
) : RecyclerView.Adapter<ViewNoteAdapter.ViewHolder>() {

    companion object {
        val ENTRY_TYPE = 0
        val NOTE_DETAILS_TYPE = 1
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

    override fun getItemCount(): Int = note.entries.size + 2

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

        fun onBind(entry: Entry) {
            val entryType = view.findViewById<TextView>(R.id.entrytype)
            val entryContent = view.findViewById<TextView>(R.id.content)

            entryType.text = entry.type.type
            entryContent.text = entry.content.content
        }
    }

    class NoteDetailViewHolder(view: View, adapter: ViewNoteAdapter, actionListener: ActionListener) :
        ViewHolder(view, adapter, actionListener) {

        private lateinit var numEntriesText: TextView

        fun onBind() {

            if (adapter.note.isNewNote()) throw IllegalStateException("Cannot view empty note.")

            numEntriesText = view.findViewById<TextView>(R.id.num_entries_text)
            val title = view.findViewById<TextView>(R.id.title)
            val editButton = view.findViewById<View>(R.id.edit_button)
            val noteType = view.findViewById<TextView>(R.id.notetype)
            val nicknames = view.findViewById<TextView>(R.id.nicknames)

            // update num entries text
            updateNumEntriesText()

            title.text = adapter.note.name
            noteType.text =
                view.context.getString(R.string.viewnote_notetype_text, adapter.note.type)

            // leave nicknames field blank if there are no nicknames
            if (adapter.note.nicknames.size > 0) {
                val nickString: String = adapter.note.nicknames.joinToString(separator = "\n")
                nicknames.text =
                    view.context.getString(R.string.viewnote_nicknames_text, nickString)
            } else {
                nicknames.text = ""
            }

            // add listener for edit
            editButton.setOnClickListener {
                actionListener.onEditButtonPress()
            }

            // TODO image
        }

        fun updateNumEntriesText() {
            val numEntries: Int = adapter.note.entries.size

            numEntriesText.text =
                view.context.getString(R.string.editnote_num_entries, numEntries)
        }

    }
}