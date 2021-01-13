package com.github.ajsnarr98.linknotes

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.github.ajsnarr98.linknotes.data.NoteCollection
import com.github.ajsnarr98.linknotes.data.NoteCollections
import com.github.ajsnarr98.linknotes.data.TagCollection
import com.github.ajsnarr98.linknotes.data.TagCollections

open class BaseViewModel : ViewModel() {
    val notesCollection: NoteCollection = NoteCollections.instance
    val tagCollection: TagCollection = TagCollections.instance

    /**
     * All lifecycle observers known by this ViewModel.
     */
    open val lifecycleObservers: MutableCollection<LifecycleObserver>
        get() = arrayListOf(notesCollection, tagCollection)
}