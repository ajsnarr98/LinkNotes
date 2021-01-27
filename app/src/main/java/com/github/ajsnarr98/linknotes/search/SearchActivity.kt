package com.github.ajsnarr98.linknotes.search

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.ajsnarr98.linknotes.BaseActivity
import com.ajsnarr.linknotes.R
import com.github.ajsnarr98.linknotes.data.Note
import com.ajsnarr.linknotes.databinding.ActivitySearchBinding
import com.github.ajsnarr98.linknotes.notes.EditNoteActivity
import com.github.ajsnarr98.linknotes.notes.ViewNoteActivity
import com.github.ajsnarr98.linknotes.util.hideKeyboardFrom
import timber.log.Timber
import java.lang.IllegalStateException

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
    private lateinit var recyclerAdapter: SearchAdapter

    private lateinit var viewModel: SearchViewModel

    private lateinit var drawerToggle: ActionBarDrawerToggle

    private lateinit var searchTypeSelections: MenuActionGroup
    private lateinit var resultOrderSelections: MenuActionGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(SearchViewModel::class.java)
        viewModel.lifecycleObservers.forEach { lifecycle.addObserver(it) }

        // setup search bar view to allow handling of back button presses within
        // keyboard
        binding.searchBarView.activity = this

        // setup recycler view
        val recyclerManager = LinearLayoutManager(this)
        recyclerAdapter = SearchAdapter(this, RecyclerActionLister(this))
        binding.recyclerView.apply {
            layoutManager = recyclerManager
            adapter = recyclerAdapter
        }

        // set up appbar/nav drawer
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setupNavDrawer()
        setupSearchBar()

        // load notes
//        viewModel.notesCollection.observe(this, {
//            // update notes based on live data changes
//            onNotesUpdate()
//        })

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

        // setup search type selection
        val searchTypeGroup = SearchType.values().map { searchType -> binding.navView.menu.findItem(searchType.resId) }
        val defaultSearchTypes = searchTypeGroup.toSet()
        searchTypeSelections = MultiSelectMenuActionGroup(searchTypeGroup, defaultSearchTypes) { selections ->
            val selectedTypes = SearchType.values().filter { searchType -> searchType.resId in selections }
            viewModel.onSearchTypesSelected(selectedTypes)
            onNotesUpdate() // load changes in recycler view to show any search changes
        }

        // setup search result order selection
        val resultOrderTypeGroup = ResultOrderType.values().map { resultOrderType -> binding.navView.menu.findItem(resultOrderType.resId) }
        val defaultResultOrder = binding.navView.menu.findItem(ResultOrderType.RECENT.resId)
        resultOrderSelections = SingleSelectMenuActionGroup(resultOrderTypeGroup, defaultResultOrder) { selection ->
            val selectedType = ResultOrderType.values().find { it.resId == selection } ?: throw IllegalStateException("Invalid resource ID")
            viewModel.onResultOrderSelected(selectedType)
            onNotesUpdate() // load changes in recycler view if order has changed
        }
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
                Timber.d("Searching with string '${s.toString()}'")
                viewModel.searchStr = s.toString()
                onNotesUpdate()
            }
        })
        binding.searchBar.setOnEditorActionListener { v, actionId, event ->
            setSearchBarActive(false)
            true
        }
    }

    /**
     * Load notes from view model, filtered for search.
     */
    private fun onNotesUpdate() {
        recyclerAdapter.updateResults(viewModel.filteredForSearch)
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
    private class RecyclerActionLister(val context: Context) : SearchAdapter.ActionListener {

        override fun onResultClick(note: Note) {
            Timber.d("onResultClick")
            context.startActivity(ViewNoteActivity.getIntent(context, note))
        }
    }
}

