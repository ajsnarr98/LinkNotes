package com.ajsnarr.peoplenotes.search

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.ajsnarr.peoplenotes.BaseActivity
import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Note
import com.ajsnarr.peoplenotes.data.Tag
import com.ajsnarr.peoplenotes.databinding.ActivitySearchBinding
import com.ajsnarr.peoplenotes.notes.EditNoteActivity
import com.ajsnarr.peoplenotes.notes.ViewNoteActivity
import com.ajsnarr.peoplenotes.util.hideKeyboardFrom
import com.ajsnarr.peoplenotes.util.max
import me.xdrop.fuzzywuzzy.FuzzySearch
import timber.log.Timber

/**
 * Min number of characters used in a search
 */
const val MIN_SEARCH_LENGTH = 3

private enum class SearchType(val text: String) {
    ALL("All Results"),
    TAG("Tag"),
    NAME("Name");

    override fun toString() = text
}

private enum class ResultOrderType(val text: String) {
    RECENT("Recent"),
    ALPHABETICAL("Alphabetical");

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

    private lateinit var binding: ActivitySearchBinding
    private lateinit var recyclerAdapter: ResultAdapter

    private lateinit var viewModel: SearchViewModel

    private lateinit var drawerToggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        // TODO: Use the ViewModel

        // setup search bar view to allow handling of back buttn presses within
        // keyboard
        binding.searchBarView.searchActivity = this

        // setup recycler view
        val recyclerManager = LinearLayoutManager(this)
        recyclerAdapter = ResultAdapter(this, RecyclerActionLister(this))
        binding.recyclerView.apply {
            layoutManager = recyclerManager
            adapter = recyclerAdapter
        }

        // set up appbar/nav drawer
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavDrawer()
        setupSearchBar()

        // setup filter dropdown
        binding.searchFiltersDropdown.adapter = ArrayAdapter<SearchType>(
            this,
            android.R.layout.simple_spinner_item, SearchType.values()
        )
        binding.searchFiltersDropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
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
        binding.addNoteButton.setOnClickListener {
            // start the edit note activity without passing in an existing note
            startActivity(EditNoteActivity.getCreateNoteIntent(this))
        }
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        drawerToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // The action bar home/up action should open or close the drawer.
        return when {
            drawerToggle.onOptionsItemSelected(item) -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onBackPressed() {
        // try to set search bar inactive
        Timber.d("onBackPressed")
        setSearchBarActive(false)
    }

    private fun setupNavDrawer() {
        drawerToggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.drawer_open,
            R.string.drawer_close
        ).apply {
            syncState()
        }
        binding.drawerLayout.addDrawerListener(drawerToggle)
    }

    private fun setupSearchBar() {
        // setup search bar
        binding.searchBar.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                setSearchBarActive(true)
            } else {
                setSearchBarActive(false)
            }
        }
        binding.searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // do nothing
            }

            override fun afterTextChanged(s: Editable?) {
                Timber.d("Searching with string '${binding.searchBar.text}'")
                loadNotes()
            }
        })
        binding.searchBar.setOnEditorActionListener { v, actionId, event ->
            setSearchBarActive(false)
            true
        }
    }


    /**
     * Filter notes based on search
     */
    private fun filterForSearch(notes: List<Note>): List<Note> {

        // TODO - improve search results shown (search "my " and "my random note" will show but not "my 1")

        val searchStr = binding.searchBar.text.toString()
        val filtered = notes.filter { note ->
            // search must be at least MIN_SEARCH_LENGTH chars (otherwise all are shown)
            (searchStr.length < MIN_SEARCH_LENGTH) or
            when (binding.searchFiltersDropdown.selectedItem) {
                SearchType.ALL -> fuzzyMatch(searchStr, note.name)
                        || note.tags.any { tag -> fuzzyMatch(searchStr, tag.text) }
                SearchType.NAME -> fuzzyMatch(searchStr, note.name)
                SearchType.TAG -> note.tags.any { tag -> fuzzyMatch(searchStr, tag.text) }
                else -> true // display everything
            }
        }

        // order by fuzzy match
        return filtered.sortedByDescending { note ->
            when (binding.searchFiltersDropdown.selectedItem) {
                SearchType.ALL -> max(
                    FuzzySearch.ratio(searchStr, note.name),
                    note.tags.map
                    { tag -> FuzzySearch.ratio(searchStr, tag.text) }.max() ?: Int.MIN_VALUE
                )
                SearchType.NAME -> FuzzySearch.ratio(searchStr, note.name)
                SearchType.TAG -> note.tags.map<Tag, Int>
                { tag -> FuzzySearch.ratio(searchStr, tag.text) }.max()
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
            Note(
                id = "123", name = "Devin James", tags = mutableListOf<Tag>(
                    Tag("friend"),
                    Tag("charlotesville"), Tag("JMU"), Tag("1"), Tag("1"),
                    Tag("1"), Tag("1")
                )
            ),
            Note(id = "1233", name = "John Smith"),
            Note(id = "1234", name = "Openheimer Shmitt")
        )
        recyclerAdapter.updateResults(notes)
    }

    private fun setSearchBarActive(isActive: Boolean) {
        // return if state of bar is not changed
        if (isActive == this.isSearchBarActive) return

        this.isSearchBarActive = isActive

        if (!isActive) {
            hideKeyboardFrom(binding.searchBar.context, binding.searchBar)
            binding.searchBar.clearFocus() // un-click from search bar
        }
    }

    // for recycler view items
    private class RecyclerActionLister(val context: Context) : ResultAdapter.ActionListener {

        override fun onResultClick(note: Note) {
            Timber.d("onResultClick")
            context.startActivity(ViewNoteActivity.getIntent(context, note))
        }
    }
}

