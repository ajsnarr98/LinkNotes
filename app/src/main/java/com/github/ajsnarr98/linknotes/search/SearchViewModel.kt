package com.github.ajsnarr98.linknotes.search

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.ajsnarr98.linknotes.Providers
import com.github.ajsnarr98.linknotes.R
import com.github.ajsnarr98.linknotes.data.Note
import com.github.ajsnarr98.linknotes.data.NoteCollection
import com.github.ajsnarr98.linknotes.data.Tag
import com.github.ajsnarr98.linknotes.data.TagCollection
import com.github.ajsnarr98.linknotes.login.AuthHandler
import com.github.ajsnarr98.linknotes.util.fuzzyMatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
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

class SearchViewModel: ViewModel() {

    val notesCollection: NoteCollection = Providers.noteCollection!!
    private val tagCollection: TagCollection = Providers.tagCollection!!
    private val unsavedChangeStore = Providers.unsavedChangeStore

    /**
     * All lifecycle observers known by this ViewModel.
     */
    val lifecycleObservers: MutableCollection<LifecycleObserver>
        get() = arrayListOf(notesCollection, tagCollection)

    var searchStr: String = ""

    private val authHandler: AuthHandler = Providers.authHandler ?: throw IllegalStateException("No auth handler instance")

    private val searchTypesSelected: MutableMap<SearchType, Boolean> = LinkedHashMap<SearchType, Boolean>().also { map ->
        SearchType.values().forEach { searchType ->  map[searchType] = false }
    }
    private var resultOrderSelected: ResultOrderType = ResultOrderType.RECENT

    /**
     * Load notes from DB and filter for search.
     */
    val filteredForSearch get() = orderForSearch(filterForSearch(notesCollection.toList()))

    private val isValidSearch: Boolean get() = searchStr.length >= MIN_SEARCH_LENGTH

    /**
     * Returns true when there were unsaved changes to a note last time the
     * app closed.
     */
    fun hasUnsavedChanges(): Boolean {
        return runBlocking {
            unsavedChangeStore?.hasUnsavedChanges()?.single() == true
        }
    }

    fun discardUnsavedChanges() {
        viewModelScope.launch (Dispatchers.IO) {
            unsavedChangeStore?.clearNote()?.collect()
        }
    }

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
                    }.maxOrNull() ?: Int.MIN_VALUE
                }
            }
        }
        return filtered.sortedByDescending { note ->
            // sort based on the max value from the available search types
            SearchType.values().map { type ->
                if (searchTypesSelected[type] == true) sortRatingMethods(type, note)
                else Int.MIN_VALUE
            }.maxOrNull()
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

    /**
     * Signs out.
     */
    fun signOut(onCompleteListener: (success: Boolean) -> Unit) {
        authHandler.signOut(onCompleteListener)
    }
}
