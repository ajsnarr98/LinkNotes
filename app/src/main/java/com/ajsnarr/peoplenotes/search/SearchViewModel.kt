package com.ajsnarr.peoplenotes.search

import androidx.lifecycle.ViewModel;
import com.ajsnarr.peoplenotes.R

enum class SearchType(val resId: Int) {
    TAG(R.id.search_filter_tags),
    TITLE(R.id.search_filter_title),
}

enum class ResultOrderType(val resId: Int) {
    RECENT(R.id.sort_order_recent),
    ALPHABETICAL(R.id.sort_order_alpha),
}

/**
 * Min number of characters used in a search
 */
const val MIN_SEARCH_LENGTH = 3

class SearchViewModel : ViewModel() {
    // TODO: Implement the ViewModel
}
