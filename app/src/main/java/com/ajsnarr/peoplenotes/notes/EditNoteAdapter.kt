package com.ajsnarr.peoplenotes.notes

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.RecyclerView
import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Entry
import com.ajsnarr.peoplenotes.data.EntryContent
import com.ajsnarr.peoplenotes.data.EntryType
import com.ajsnarr.peoplenotes.databinding.ItemEditnoteAddBtnBinding
import com.ajsnarr.peoplenotes.databinding.ItemEditnoteDetailsBinding
import com.ajsnarr.peoplenotes.databinding.ItemEditnoteEntryBinding
import timber.log.Timber
import java.lang.IllegalArgumentException


class EditNoteAdapter(private val viewModel: EditNoteViewModel,
                      private val actionListener: ActionListener) : RecyclerView.Adapter<EditNoteAdapter.ViewHolder>() {

    companion object {
        const val ENTRY_TYPE = 0
        const val NOTE_DETAILS_TYPE = 1
        const val ADD_ENTRY_BUTTON_TYPE = 2
    }

    /**
     * Pass this into an instance of EditNoteAdapter to subscribe to events.
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
         * Called when an entry is edited.
         */
        fun onEditEntry(entry: Entry)

        /**
         * Called when someone presses the delete button for the note.
         */
        fun onDeletePress()

        /**
         * Called when the user clicks the delete entry button.
         */
        fun onDeleteEntryPress(entry: Entry)

        /**
         * Called when the save button is pressed.
         */
        fun onSaveButtonPress()

        /**
         * Called when the notetype is changed.
         */
        fun onSetNoteType(noteType: String)

        /**
         * Set note title.
         */
        fun onSetTitle(title: String)

        /**
         * Called to setup note type auto completion.
         */
        fun onSetupNoteTypes(noteTypeField: AutoCompleteTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return when (viewType) {
            ENTRY_TYPE -> EntryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_editnote_entry, parent, false),
                viewModel, actionListener
            )
            NOTE_DETAILS_TYPE -> NoteDetailViewHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.item_editnote_details, parent, false),
                    viewModel, actionListener
            )
            ADD_ENTRY_BUTTON_TYPE -> AddButtonViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_editnote_add_btn, parent, false),
                viewModel, actionListener
            )
            else -> throw IllegalArgumentException("Unhandled viewType: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> NOTE_DETAILS_TYPE
            itemCount - 1 -> ADD_ENTRY_BUTTON_TYPE
            else -> ENTRY_TYPE
        }
    }

    override fun getItemCount(): Int = viewModel.note.entries.size + 2

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return when (holder) {
            is EntryViewHolder      -> holder.onBind(viewModel.note.entries[position - 1])
            is NoteDetailViewHolder -> holder.onBind()
            is AddButtonViewHolder  -> holder.onBind()
            else -> throw IllegalArgumentException("Unhandled viewType: ${holder::class.qualifiedName}")
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.onDetach()
    }

    abstract class ViewHolder(
        protected val view: View,
        protected val viewModel: EditNoteViewModel,
        protected val actionListener: ActionListener
    ) : RecyclerView.ViewHolder(view) {

        /**
         * Used for watching for after text changes, and no other time.
         */
        protected class AfterTextChangedWatcher(val onAfterTextChanged: (Editable?) -> Any) : TextWatcher {
            override fun afterTextChanged(s: Editable?) { onAfterTextChanged(s) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        /**
         * Called when this view is getting detached from its window.
         */
        open fun onDetach() {}
    }


    class EntryViewHolder(view: View, viewModel: EditNoteViewModel, actionListener: ActionListener) :
        ViewHolder(view, viewModel, actionListener) {

        private lateinit var binding: ItemEditnoteEntryBinding

        private var entryTypeListener: TextWatcher? = null
        private var contentListener: TextWatcher? = null
        private var deleteListener: View.OnClickListener? = null

        fun onBind(entry: Entry) {
            Timber.d("onBindEntry")
            binding = ItemEditnoteEntryBinding.bind(view)

            // remove old update listeners before refreshing views
            removeListeners()

            // add stored content
            binding.content.text.clear()
            binding.content.text.append(entry.content.value)
            binding.entryType.text.clear()
            binding.entryType.text.append(entry.type.value)

            // add listeners
            entryTypeListener = AfterTextChangedWatcher {
                val type: String? = it?.toString()
                entry.type = if (type != null) EntryType(value = type) else EntryType.DEFAULT
                actionListener.onEditEntry(entry)
            }.also { binding.entryType.addTextChangedListener(it) }

            contentListener = AfterTextChangedWatcher {
                val content: String? = it?.toString()
                entry.content = if (content != null) EntryContent(value = content) else EntryContent.EMPTY
                actionListener.onEditEntry(entry)
            }.also { binding.content.addTextChangedListener(it) }

            binding.deleteButton.setOnClickListener { actionListener.onDeleteEntryPress(entry) }
        }

        override fun onDetach() {
            removeListeners()
        }

        private fun removeListeners() {
            binding.entryType.removeTextChangedListener(entryTypeListener)
            binding.content.removeTextChangedListener(contentListener)
            binding.deleteButton.setOnClickListener(null)
        }
    }

    class NoteDetailViewHolder(view: View, viewModel: EditNoteViewModel, actionListener: ActionListener) :
        ViewHolder(view, viewModel, actionListener) {

        private lateinit var binding: ItemEditnoteDetailsBinding

        private var titleWatcher: TextWatcher? = null
        private var noteTypeWatcher: TextWatcher? = null

        fun onBind() {
            Timber.d("onBindNoteDetails")
            binding = ItemEditnoteDetailsBinding.bind(view)

            // remove old update listeners before refreshing views
            removeListeners()

//            // setup popup button
//            binding.tagsPopupButton.setOnClickListener {
//                actionListener.onAddTag()
//            }

            // add to note type autocomplete text view
            actionListener.onSetupNoteTypes(binding.noteTypeInput)

            // update fields
            if (viewModel.note.isNewNote()) {
                // hide delete button if note is new
                binding.deleteButton.visibility = View.INVISIBLE
            } else {
                // setup the delete button
                binding.deleteButton.visibility = View.VISIBLE
                binding.deleteButton.setOnClickListener { actionListener.onDeletePress() }
            }

            binding.titleInput.text.clear()
            binding.titleInput.text.append(viewModel.note.name)

            binding.noteTypeInput.text.clear()
            binding.noteTypeInput.text.append(viewModel.note.type)

            // TODO nicknames

            // TODO image

            // update num entries text
            binding.numEntriesText.text =
                view.context.getString(R.string.editnote_num_entries, viewModel.note.entries.size)

            // setup update listeners
            titleWatcher = AfterTextChangedWatcher {
                actionListener.onSetTitle(it?.toString() ?: "")
            }.also { binding.titleInput.addTextChangedListener(it) }

            noteTypeWatcher = AfterTextChangedWatcher {
                actionListener.onSetNoteType(it?.toString() ?: "")
            }.also { binding.noteTypeInput.addTextChangedListener(it) }
        }

        override fun onDetach() {
            removeListeners()
        }

        private fun removeListeners() {
            if (titleWatcher != null) binding.titleInput.removeTextChangedListener(titleWatcher)
            if (noteTypeWatcher != null) binding.noteTypeInput.removeTextChangedListener(noteTypeWatcher)
            binding.deleteButton.setOnClickListener(null)
        }
    }

    class AddButtonViewHolder(view: View, viewModel: EditNoteViewModel, actionListener: ActionListener) :
            ViewHolder(view, viewModel, actionListener) {

        private lateinit var binding: ItemEditnoteAddBtnBinding

        fun onBind() {
            Timber.d("OnBindAddButton")
            binding = ItemEditnoteAddBtnBinding.bind(view)

            // set up add entry button
            binding.addEntryButton.setOnClickListener {
                actionListener.onAddButtonPress()
            }
        }
    }
}
