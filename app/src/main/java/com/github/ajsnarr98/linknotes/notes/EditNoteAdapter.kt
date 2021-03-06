package com.github.ajsnarr98.linknotes.notes

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import androidx.recyclerview.widget.RecyclerView
import com.github.ajsnarr98.linknotes.R
import com.github.ajsnarr98.linknotes.data.Entry
import com.github.ajsnarr98.linknotes.data.EntryContent
import com.github.ajsnarr98.linknotes.data.EntryType
import com.github.ajsnarr98.linknotes.data.Tag
import com.github.ajsnarr98.linknotes.databinding.ItemEditnoteAddBtnBinding
import com.github.ajsnarr98.linknotes.databinding.ItemEditnoteDetailsBinding
import com.github.ajsnarr98.linknotes.databinding.ItemEditnoteEntryBinding
import com.google.android.material.chip.Chip
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.util.*


class EditNoteAdapter(private val viewModel: EditNoteViewModel,
                      private val actionListener: ActionListener) : RecyclerView.Adapter<EditNoteAdapter.ViewHolder>() {

    companion object {
        const val DEFAULT_ENTRY_TYPE = 0
        const val IMAGE_ENTRY_TYPE = -1
        const val NOTE_DETAILS_TYPE = 1
        const val ADD_ENTRY_BUTTON_TYPE = 2
        const val BOTTOM_SPACING_TYPE = 3
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
        fun onAddTags()

        /**
         * Called when an entry is edited. If successful edit, return updated
         * entry, else return null.
         */
        fun onEditEntry(entry: Entry): Entry?

        /**
         * Called when someone presses the delete button for the note.
         */
        fun onDeletePress()

        /**
         * Called when the user clicks the delete entry button.
         */
        fun onDeleteEntryPress(entry: Entry)

        /**
         * Called when the user tries to remove a tag from this note.
         */
        fun onRemoveTag(tag: Tag)

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
            DEFAULT_ENTRY_TYPE -> EntryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_editnote_entry, parent, false),
                actionListener,
            )
            IMAGE_ENTRY_TYPE -> SpecialEntryViewHolder(
                ImageEntryView(
                    context = parent.context,
                    isEditable = true,
                    addImageListener = { entry: Entry, imageUrl: String ->
                        actionListener.onEditEntry(entry.copy().apply { appendImage(imageUrl) })
                    },
                    onDeleteEntryPress = { entry -> actionListener.onDeleteEntryPress(entry) },
                )
            )
            NOTE_DETAILS_TYPE -> NoteDetailViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_editnote_details, parent, false),
                viewModel,
                actionListener,
            )
            ADD_ENTRY_BUTTON_TYPE -> AddButtonViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_editnote_add_btn, parent, false),
                actionListener,
            )
            BOTTOM_SPACING_TYPE -> BasicViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_editnote_empty_spacing_bottom, parent, false),
            )
            else -> throw IllegalArgumentException("Unhandled viewType: $viewType")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (position) {
            0 -> NOTE_DETAILS_TYPE
            itemCount - 2 -> ADD_ENTRY_BUTTON_TYPE
            itemCount - 1 -> BOTTOM_SPACING_TYPE
            else -> when (viewModel.note.entries[position - 1].type) {
                is EntryType.IMAGES -> IMAGE_ENTRY_TYPE
                is EntryType.DEFAULT, is EntryType.CUSTOM -> DEFAULT_ENTRY_TYPE
            }
        }
    }

    override fun getItemCount(): Int = viewModel.note.entries.size + 3

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return when (holder) {
            is EntryViewHolder        -> holder.onBind(viewModel.note.entries[position - 1])
            is SpecialEntryViewHolder -> holder.onBind(viewModel.note.entries[position - 1])
            is NoteDetailViewHolder   -> holder.onBind()
            is AddButtonViewHolder    -> holder.onBind()
            is BasicViewHolder        -> { /* no-op */ }
            else -> throw IllegalArgumentException("Unhandled viewType: ${holder::class.qualifiedName}")
        }
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        holder.onDetach()
    }

    abstract class ViewHolder(
        view: View,
    ) : RecyclerView.ViewHolder(view) {

        /**
         * Used for watching for after text changes, and no other time.
         */
        protected class AfterTextChangedWatcher(val onAfterTextChanged: (Editable?) -> Unit) : TextWatcher {
            override fun afterTextChanged(s: Editable?) { onAfterTextChanged(s) }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        /**
         * Called when this view is getting detached from its window.
         */
        open fun onDetach() {}
    }

    class BasicViewHolder(view: View) : ViewHolder(view)

    /** Holds a special type of entry view that has implemented EntryView. */
    class SpecialEntryViewHolder(val entryView: EntryView) : ViewHolder(entryView.view) {
        fun onBind(entry: Entry) {
            entryView.bind(entry)
        }
    }

    class EntryViewHolder(view: View, private val actionListener: ActionListener) :
        ViewHolder(view) {

        private val binding = ItemEditnoteEntryBinding.bind(itemView)

        private var entryTypeListener: TextWatcher? = null
        private var contentListener: TextWatcher? = null
        private lateinit var entry: Entry

        fun onBind(entry: Entry) {
            Timber.d("onBindEntry")
            this.entry = entry

            // remove old update listeners before refreshing views
            removeListeners()

            // add stored content
            binding.content.text?.clear()
            binding.content.text?.append(this.entry.content.value)
            binding.entryType.text?.clear()
            binding.entryType.text?.append(this.entry.type.value)

            // add listeners
            entryTypeListener = AfterTextChangedWatcher {
                val type: String? = it?.toString()?.toLowerCase(Locale.US)
                val newEntry = this.entry.copy()
                newEntry.type = if (type != null) EntryType.forValue(type) else EntryType.DEFAULT()
                this.entry = actionListener.onEditEntry(newEntry) ?: this.entry // update stored entry if successful
            }.also { binding.entryType.addTextChangedListener(it) }

            contentListener = AfterTextChangedWatcher {
                val content: String? = it?.toString()
                val newEntry = this.entry.copy()
                newEntry.content = if (content != null) EntryContent(value = content) else EntryContent.EMPTY
                this.entry = actionListener.onEditEntry(newEntry) ?: this.entry // update stored entry if successful
            }.also { binding.content.addTextChangedListener(it) }

            binding.deleteButton.setOnClickListener { actionListener.onDeleteEntryPress(this.entry.copy()) }
        }

        private fun removeListeners() {
            if (entryTypeListener != null) binding.entryType.removeTextChangedListener(entryTypeListener)
            if (contentListener != null) binding.content.removeTextChangedListener(contentListener)
            binding.deleteButton.setOnClickListener(null)
        }
    }

    class NoteDetailViewHolder(view: View, private val viewModel: EditNoteViewModel, private val actionListener: ActionListener) :
        ViewHolder(view) {

        private val binding = ItemEditnoteDetailsBinding.bind(itemView)

        private var titleWatcher: TextWatcher? = null
        private var noteTypeWatcher: TextWatcher? = null
        private var addNewTagsChip: Chip? = null

        fun onBind() {
            Timber.d("onBindNoteDetails")

            // remove old update listeners before refreshing views
            removeListeners()

            // setup tags
            if (viewModel.note.tags.isEmpty()) {
                binding.tagChipGroup.visibility = View.GONE
                binding.emptyAddTagsButton.visibility = View.VISIBLE
                binding.emptyAddTagsButton.setOnClickListener { actionListener.onAddTags() }
            } else {
                binding.tagChipGroup.visibility = View.VISIBLE
                binding.emptyAddTagsButton.visibility = View.GONE
                binding.tagChipGroup.setOnAddButtonClickListener { actionListener.onAddTags() }
                binding.tagChipGroup.setOnTagClickListener { tag -> actionListener.onRemoveTag(tag) }

                // refresh tags
                binding.tagChipGroup.setTags(viewModel.note.tags)
            }

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

            binding.titleInput.text?.clear()
            binding.titleInput.text?.append(viewModel.note.name)

            binding.noteTypeInput.text?.clear()
            binding.noteTypeInput.text?.append(viewModel.note.type)

            // TODO nicknames

            // TODO image

            // update num entries text
            binding.numEntriesText.text =
                itemView.context.getString(R.string.editnote_num_entries, viewModel.note.entries.size)

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
            binding.emptyAddTagsButton.setOnClickListener(null)
            addNewTagsChip?.setOnClickListener(null)
        }
    }

    class AddButtonViewHolder(view: View, private val actionListener: ActionListener) :
            ViewHolder(view) {

        private lateinit var binding: ItemEditnoteAddBtnBinding

        fun onBind() {
            Timber.d("OnBindAddButton")
            binding = ItemEditnoteAddBtnBinding.bind(itemView)

            // set up add entry button
            binding.addEntryButton.setOnClickListener {
                actionListener.onAddButtonPress()
            }
        }
    }
}
