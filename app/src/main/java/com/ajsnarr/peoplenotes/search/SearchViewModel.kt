package com.ajsnarr.peoplenotes.search

import androidx.lifecycle.ViewModel
import com.ajsnarr.peoplenotes.R
import com.ajsnarr.peoplenotes.data.Note
import com.ajsnarr.peoplenotes.data.NoteCollection
import com.ajsnarr.peoplenotes.data.Tag
import me.xdrop.fuzzywuzzy.FuzzySearch
import java.util.*
import kotlin.collections.LinkedHashMap

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

    val mNotesCollection = NoteCollection.instance
    var searchStr: String = ""
    val searchTypesSelected: MutableMap<SearchType, Boolean> = LinkedHashMap<SearchType, Boolean>().also { map ->
        SearchType.values().forEach { searchType ->  map[searchType] = false }
    }
    var resultOrderSelected: ResultOrderType = ResultOrderType.RECENT

    /**
     * Load notes from DB and filter for search.
     */
    val filteredForSearch get() = orderForSearch(filterForSearch(mNotesCollection.toList()))

    private val isValidSearch: Boolean get() = searchStr.length >= MIN_SEARCH_LENGTH

    fun onSearchTypesSelected(selectedTypes: List<SearchType>) {
        // reset search type values
        SearchType.values().forEach { searchType ->  searchTypesSelected[searchType] = false }

        // fill set search types
        selectedTypes.forEach { searchType -> searchTypesSelected[searchType] = true }
    }

    fun onResultOrderSelected(selectedType: ResultOrderType) {
        resultOrderSelected = selectedType
    }

    /**
     * Filter notes based on search
     */
    private fun filterForSearch(notes: List<Note>): List<Note> {

        // TODO - improve search results shown (search "my " and "my random note" will show but not "my 1")

        val filterMethods: (type: SearchType, note: Note) -> Boolean = { type, note ->
            when (type) {
                SearchType.TITLE -> fuzzyMatch(searchStr, note.name)
                SearchType.TAG -> note.tags.any { tag -> fuzzyMatch(searchStr, tag.text) }
            }
        }

        val filtered = notes.filter { note ->
            // search must be at least MIN_SEARCH_LENGTH chars (otherwise all are shown)
            (!isValidSearch) ||  SearchType.values().any { type ->
                // if search type is enabled, run filterMethod for it
                if (searchTypesSelected[type] == true) filterMethods(type, note)
                else false
            }
        }

        // order by fuzzy match
        val sortRatingMethods: (type: SearchType, note: Note) -> Int = { type, note ->
            when (type) {
                SearchType.TITLE -> FuzzySearch.ratio(searchStr, note.name)
                SearchType.TAG -> {
                    note.tags.map<Tag, Int> { tag ->
                        FuzzySearch.ratio(searchStr, tag.text)
                    }.max() ?: Int.MIN_VALUE
                }
            }
        }
        return filtered.sortedByDescending { note ->
            // sort based on the max value from the available search types
            SearchType.values().map { type ->
                if (searchTypesSelected[type] == true) sortRatingMethods(type, note)
                else Int.MIN_VALUE
            }.max()
        }
    }

    private fun orderForSearch(notes: List<Note>): List<Note> {
        return notes.sortedBy { note ->
            when (resultOrderSelected) {
                ResultOrderType.RECENT -> (Long.MAX_VALUE - note.lastDateEdited.time).toString()
                ResultOrderType.ALPHABETICAL -> note.name.toUpperCase(Locale.ROOT)
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
}
