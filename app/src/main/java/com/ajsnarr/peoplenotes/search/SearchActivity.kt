package com.ajsnarr.peoplenotes.search

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.ajsnarr.peoplenotes.R


class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        // TODO: Use the ViewModel
    }
}
