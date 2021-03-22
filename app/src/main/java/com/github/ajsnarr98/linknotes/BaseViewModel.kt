package com.github.ajsnarr98.linknotes

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import com.github.ajsnarr98.linknotes.data.NoteCollection
import com.github.ajsnarr98.linknotes.data.TagCollection

open class BaseViewModel : ViewModel() {
    val notesCollection: NoteCollection = Providers.noteCollection!!
    val tagCollection: TagCollection = Providers.tagCollection!!

    /**
     * All lifecycle observers known by this ViewModel.
     */
    open val lifecycleObservers: MutableCollection<LifecycleObserver>
        get() = arrayListOf(notesCollection, tagCollection)
}