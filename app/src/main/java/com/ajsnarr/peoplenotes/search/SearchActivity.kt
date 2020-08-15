package com.ajsnarr.peoplenotes.search

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajsnarr.peoplenotes.BaseActivity
import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Note
import com.ajsnarr.peoplenotes.data.Tag
import com.ajsnarr.peoplenotes.notes.EditNoteActivity
import com.ajsnarr.peoplenotes.util.hideKeyboardFrom
import com.ajsnarr.peoplenotes.util.max
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.activity_search.*
import me.xdrop.fuzzywuzzy.FuzzySearch
import timber.log.Timber
import java.lang.IllegalArgumentException


/**
 * Min number of characters used in a search
 */
const val MIN_SEARCH_LENGTH = 3

private enum class SearchType(val text: String) {
    ALL("All Results"),
    TAG("TAG"),
    NAME("NAME");

    override fun toString() = text
}

class SearchActivity : BaseActivity() {

    companion object {
        fun getSearchIntent(context: Context): Intent {
            return Intent(context, SearchActivity::class.java)
        }
    }

    private val SEARCH_ICON = android.R.drawable.ic_menu_search
    private val X_ICON = android.R.drawable.ic_menu_close_clear_cancel

    private var isSearchBarActive = false

    private lateinit var viewModel: SearchViewModel

    private lateinit var searchBar: TextInputEditText
    private lateinit var searchFiltersDropdown: Spinner
    private lateinit var recyclerAdapter: ResultAdapter
    private lateinit var addNoteButton: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        // TODO: Use the ViewModel

        // setup search bar view to allow handling of back buttn presses within
        // keyboard
        search_bar_view.searchActivity = this

        // setup recycler view
        val recycler = recycler_view
        val recyclerManager = LinearLayoutManager(this)
        recyclerAdapter = ResultAdapter(this, RecyclerActionLister())
        recycler.apply {
            layoutManager = recyclerManager
            adapter = recyclerAdapter
        }

        // setup search bar
        searchBar = search_bar
        searchBar.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                setSearchBarActive(true)
            } else {
                setSearchBarActive(false)
            }
        }
        searchBar.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // do nothing
            }
            override fun afterTextChanged(s: Editable?) {
                Timber.d("Searching with string '${searchBar.text}'")
                loadNotes()
            }
        })
        searchBar.setOnEditorActionListener { v, actionId, event ->
            setSearchBarActive(false)
            true
        }

        // setup filter dropdown
        searchFiltersDropdown = search_filters_dropdown
        searchFiltersDropdown.adapter = ArrayAdapter<SearchType>(this,
            android.R.layout.simple_spinner_item, SearchType.values())
        searchFiltersDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (view is AppCompatTextView) {
                    loadNotes() // search again
                }
            }
        }

        // load notes
//        loadDefaultNotes()
        mNotesCollection.observe(this, Observer { updatedNotes ->
            // update notes based on live data changes
            loadNotes()
        })

        // setup add note button
        addNoteButton = add_note_button
        addNoteButton.setOnClickListener {
            // start the edit note activity without passing in an existing note
            startActivity(
                Intent(this, EditNoteActivity::class.java)
            )
        }
    }

    override fun onBackPressed() {
        // try to set search bar inactive
        Timber.d("onBackPressed")
        setSearchBarActive(false)
    }

    /**
     * Filter notes based on search
     */
    private fun filterForSearch(notes: List<Note>): List<Note> {

        // TODO - improve search results shown (search "my " and "my random note" will show but not "my 1")

        val searchStr = searchBar.text.toString()
        val filtered = notes.filter {note ->
            // search must be at least MIN_SEARCH_LENGTH chars (otherwise all are shown)
            (searchStr.length < MIN_SEARCH_LENGTH) or
            when (searchFiltersDropdown.selectedItem) {
                SearchType.ALL -> fuzzyMatch(searchStr, note.name)
                        || note.tags.any { tag -> fuzzyMatch(searchStr, tag.text) }
                SearchType.NAME ->  fuzzyMatch(searchStr, note.name)
                SearchType.TAG -> note.tags.any { tag -> fuzzyMatch(searchStr, tag.text) }
                else -> true // display everything
            }
        }

        // order by fuzzy match
        return filtered.sortedByDescending { note ->
            when (searchFiltersDropdown.selectedItem) {
                SearchType.ALL -> max(
                    FuzzySearch.ratio(searchStr, note.name),
                    note.tags.map
                        {tag -> FuzzySearch.ratio(searchStr, tag.text) }.max() ?: Int.MIN_VALUE
                )
                SearchType.NAME -> FuzzySearch.ratio(searchStr, note.name)
                SearchType.TAG -> note.tags.map<Tag, Int>
                    {tag -> FuzzySearch.ratio(searchStr, tag.text) }.max()
                else -> throw IllegalArgumentException("Unrecognized search filter type")
            }
        }
    }

    private fun fuzzyMatch(searchStr: String, matchTo: String): Boolean {
        return if (searchStr.length <= matchTo.length) {
            // match for part of given strs if search string is less than string to be matched to
            FuzzySearch.partialRatio(searchStr, matchTo) >= 70
        } else {
            // make search more condensed as search string gets bigger
            FuzzySearch.ratio(searchStr, matchTo) >= 85
        }
    }

    /**
     * Load notes from DB and filter for search.
     */
    private fun loadNotes() {
        recyclerAdapter.updateResults(filterForSearch(mNotesCollection.toList()))
    }

    private fun loadDefaultNotes() {
        val notes = mutableListOf<Note>(
            Note(id="123", name="Devin James", tags=mutableListOf<Tag>(Tag("friend"),
                Tag("charlotesville"), Tag("JMU"), Tag("1"), Tag("1"),
                Tag("1"), Tag("1"))),
            Note(id="1233", name="John Smith"),
            Note(id="1234", name="Openheimer Shmitt")
        )
        recyclerAdapter.updateResults(notes)
    }

    private fun setSearchBarActive(isActive: Boolean) {
        // return if state of bar is not changed
        if (isActive == this.isSearchBarActive) return

        this.isSearchBarActive = isActive

        if (!isActive) {
            hideKeyboardFrom(searchBar.context, search_bar)
            searchBar.clearFocus() // un-click from search bar
        }
    }

    // for recycler view items
    private class RecyclerActionLister() : ResultAdapter.ActionListener {

    }
}

