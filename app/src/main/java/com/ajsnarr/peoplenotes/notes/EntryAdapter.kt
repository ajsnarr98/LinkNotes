package com.ajsnarr.peoplenotes.notes

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.MultiAutoCompleteTextView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Entry
import com.ajsnarr.peoplenotes.data.Note
import com.ajsnarr.peoplenotes.util.MultiEditText
import timber.log.Timber
import java.lang.IllegalArgumentException


class EntryAdapter(private val note: Note,
                   private val actionListener: ActionListener) : RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    private lateinit var mNoteDetailsViewHolder: EntryViewHolder

    init {

    }

    companion object {
            val ENTRY_TYPE            = 0
            val NOTE_DETAILS_TYPE     = 1
            val ADD_ENTRY_BUTTON_TYPE = 2
    }

    /**
     * Pass this into an instance of EntryAdapter to subscribe to events.
     */
    interface ActionListener {

        /**
         * Called when the add new entry button is pressed.
         */
        fun onAddButtonPress()

        /**
         * Called when the tag popup button is pressed.
         */
        fun onAddTag()

        /**
         * Called when the save button is pressed.
         */
        fun onSaveButtonPress()

        /**
         * Set note title.
         */
        fun onSetTitle(title: String)

        /**
         * Called to setup note type auto completion.
         */
        fun onSetupNoteTypes(noteTypeField: AutoCompleteTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {

        return when (viewType) {
            ENTRY_TYPE            -> EntryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_editnote_entry, parent, false),
                this, actionListener
            )
            NOTE_DETAILS_TYPE     -> {
                mNoteDetailsViewHolder = EntryViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_editnote_details, parent, false),
                this, actionListener)
                mNoteDetailsViewHolder
            }
            ADD_ENTRY_BUTTON_TYPE -> EntryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_editnote_add_btn, parent, false),
                this, actionListener
            )
            else                  -> throw IllegalArgumentException("Unhandled viewType: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0           -> NOTE_DETAILS_TYPE
            itemCount-1 -> ADD_ENTRY_BUTTON_TYPE
            else        -> ENTRY_TYPE
        }
    }

    override fun getItemCount(): Int = note.entries.size + 2

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val viewType: Int = getItemViewType(position)
        return when (viewType) {
            ENTRY_TYPE            -> holder.onBindEntry(note.entries[position - 1])
            NOTE_DETAILS_TYPE     -> holder.onBindNoteDetails()
            ADD_ENTRY_BUTTON_TYPE -> holder.onBindAddButton()
            else                  -> throw IllegalArgumentException("Unhandled viewType: $viewType")
        }
    }

    private fun updateNumEntriesText() {
        mNoteDetailsViewHolder.updateNumEntriesText()
    }

    class EntryViewHolder(val view: View, val adapter: EntryAdapter, val actionListener: ActionListener)
        : RecyclerView.ViewHolder(view) {

        private lateinit var numEntriesText: TextView

        fun onBindEntry(entry: Entry) {
            val entry_type = view.findViewById<EditText>(R.id.textinput_editnote_entrytype)
            val entry_content = view.findViewById<EditText>(R.id.edittext_editnote_content)

            entry_content.text.append(entry.content.toString())
        }
        fun onBindNoteDetails() {

            Timber.d("onBindNoteDetails")

            numEntriesText = view.findViewById<TextView>(R.id.num_entries_text)
            val titleInput = view.findViewById<EditText>(R.id.title_input)
            val tagsPopupButton = view.findViewById<View>(R.id.save_button)
            val saveButton = view.findViewById<View>(R.id.save_button)
            val noteTypeInput = view.findViewById<AutoCompleteTextView>(R.id.notetype_auto_input)
            val nickNameInput = view.findViewById<MultiEditText>(R.id.nicknames_auto_multi_input)

            // setup popup button
            tagsPopupButton.setOnClickListener {
                actionListener.onAddTag()
            }

            // setup save button
            saveButton.setOnClickListener {
                actionListener.onSaveButtonPress()
            }

            // add to note type autocomplete text view
            actionListener.onSetupNoteTypes(noteTypeInput)

            // update fields
            if (false == adapter.note.isNewNote()) {
                // if not a new note
                titleInput.text.clear()
                titleInput.text.append(adapter.note.name)

                val isDefaultNoteType = adapter.note.type.isBlank() && !adapter.note.isNewNote()
                val noteType = if (isDefaultNoteType) Note.DEFAULT_NOTE_TYPE else adapter.note.type
                noteTypeInput.text.clear()
                noteTypeInput.text.append(noteType)

                // TODO nicknames

                // TODO image
            }

            // update num entries text
            updateNumEntriesText()

            // setup update listeners
            titleInput.addTextChangedListener(TitleWatcher(actionListener))
        }


        fun updateNumEntriesText() {
            val numEntries: Int = adapter.note.entries.size

            numEntriesText.text =
                view.context.getString(R.string.editnote_num_entries, numEntries)
        }

        class TitleWatcher(val actionListener: ActionListener) : TextWatcher {
            override fun afterTextChanged(s: Editable?) { actionListener.onSetTitle(s?.toString() ?: "") }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        fun onBindAddButton() {

            Timber.d("OnBindAddButton")

            val addEntryButton = view.findViewById<View>(R.id.add_entry_button)

            // set up add entry button
            addEntryButton.setOnClickListener {
                actionListener.onAddButtonPress()
                adapter.notifyDataSetChanged()
                updateNumEntriesText()
            }
        }
    }
}
