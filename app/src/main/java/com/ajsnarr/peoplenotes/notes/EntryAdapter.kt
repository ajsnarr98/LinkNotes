package com.ajsnarr.peoplenotes.notes

import android.text.Editable
import android.text.TextWatcher
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
import java.lang.IllegalArgumentException


class EntryAdapter(private val entries: MutableList<Entry>,
                   private val actionListener: ActionListener) : RecyclerView.Adapter<EntryAdapter.EntryViewHolder>() {

    private lateinit var mNoteDetails: View
    private lateinit var mNumEntriesText: TextView

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
        fun onCreateTagsPopup()

        /**
         * Set mNote title.
         */
        fun onSetTitle(title: String)

        /**
         * Called to setup mNote type auto completion.
         */
        fun onSetupNoteTypes(noteTypeField: AutoCompleteTextView)

        /**
         * Called to setup nickname auto completion.
         */
        fun onSetupNicknames(nicknameField: MultiAutoCompleteTextView)

        /**
         * Called when updating number of entries
         */
        fun onUpdateNumEntriesText(numEntriesText: TextView, numEntries: Int)
    }

    fun updateNumEntriesText() {
        actionListener.onUpdateNumEntriesText(mNumEntriesText, entries.size)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryViewHolder {

        return when (viewType) {
            ENTRY_TYPE            -> EntryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_editnote_entry, parent, false),
                this, actionListener
            )
            NOTE_DETAILS_TYPE     -> {
                mNoteDetails = LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_editnote_details, parent, false)
                mNumEntriesText = mNoteDetails.findViewById(R.id.num_entries_text)
                EntryViewHolder(mNoteDetails, this, actionListener)
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

    override fun getItemCount(): Int = entries.size + 2

    override fun onBindViewHolder(holder: EntryViewHolder, position: Int) {
        val viewType: Int = getItemViewType(position)
        return when (viewType) {
            ENTRY_TYPE            -> holder.onBindEntry(entries[position - 1])
            NOTE_DETAILS_TYPE     -> holder.onBindNoteDetails()
            ADD_ENTRY_BUTTON_TYPE -> holder.onBindAddButton()
            else                  -> throw IllegalArgumentException("Unhandled viewType: $viewType")
        }
    }

    class EntryViewHolder(val view: View, val adapter: EntryAdapter, val actionListener: ActionListener)
        : RecyclerView.ViewHolder(view) {

        fun onBindEntry(entry: Entry) {
            val entry_type = view.findViewById<EditText>(R.id.textinput_editnote_entrytype)
            val entry_content = view.findViewById<EditText>(R.id.edittext_editnote_content)

            entry_content.text.append(entry.content.toString())
        }
        fun onBindNoteDetails() {

            val titleInput = view.findViewById<EditText>(R.id.title_input)
            val tagsPopupButton = view.findViewById<View>(R.id.tags_popup_btn)
            val noteTypeInput = view.findViewById<AutoCompleteTextView>(R.id.notetype_auto_input)
            val nickNameInput = view.findViewById<MultiAutoCompleteTextView>(R.id.nicknames_auto_multi_input)

            // setup popup button
            tagsPopupButton.setOnClickListener {
                actionListener.onCreateTagsPopup()
            }

            // add to mNote type autocomplete text view
            actionListener.onSetupNoteTypes(noteTypeInput)

            // add to nickname autocomplete text view
            actionListener.onSetupNicknames(nickNameInput)

            // update num entries text
            adapter.updateNumEntriesText()

            // setup update listeners
            titleInput.addTextChangedListener(TitleWatcher(actionListener))
        }

        class TitleWatcher(val actionListener: ActionListener) : TextWatcher {
            override fun afterTextChanged(s: Editable?) { actionListener.onSetTitle(s?.toString() ?: "") }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        }

        fun onBindAddButton() {

            val addEntryButton = view.findViewById<View>(R.id.add_entry_button)

            // set up add entry button
            addEntryButton.setOnClickListener {
                actionListener.onAddButtonPress()
                adapter.notifyDataSetChanged()
                adapter.updateNumEntriesText()
            }
        }
    }
}
