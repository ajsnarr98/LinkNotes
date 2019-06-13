package com.ajsnarr.peoplenotes.search

import android.content.Context
import android.graphics.drawable.Icon
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.renderscript.ScriptGroup
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.hideKeyboard
import com.ajsnarr.peoplenotes.hideKeyboardFrom
import kotlinx.android.synthetic.main.activity_search.*


private const val TAG = "SearchActivity"

class SearchActivity : AppCompatActivity() {

    private val SEARCH_ICON = android.R.drawable.ic_menu_search
    private val X_ICON = android.R.drawable.ic_menu_close_clear_cancel

    private var isSearchBarActive = false

    private lateinit var viewModel: SearchViewModel

    private lateinit var searchBar: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        // TODO: Use the ViewModel

        // setup search bar view to allow handling of back buttn presses within
        // keyboard
        search_bar_view.searchActivity = this

        // setup search bar
        searchBar = search_bar
        searchBar.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                setSearchBarActive(true)
            } else {
                setSearchBarActive(false)
            }
        }
        searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                setSearchBarActive(false)
                true
            } else {
                false
            }
        }
    }

    override fun onBackPressed() {
        // try to set search bar inactive
        Log.d(TAG, "onBackPressed")
        setSearchBarActive(false)
    }

    private fun setSearchBarActive(isActive: Boolean) {
        // return if state of bar is not changed
        if (isActive == this.isSearchBarActive) return

        this.isSearchBarActive = isActive

        if (!isActive) {
            hideKeyboardFrom(searchBar.context, search_bar)
            searchBar.clearFocus()
        }

    }
}
