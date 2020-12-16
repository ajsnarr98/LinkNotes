package com.github.ajsnarr98.linknotes.util.dialogs

import androidx.lifecycle.MutableLiveData
import com.github.ajsnarr98.linknotes.BaseViewModel
import com.github.ajsnarr98.linknotes.data.Tag

class AddTagDialogViewModel : BaseViewModel() {

    /**
     * Tags to return from the dialog when finished.
     */
    val tagQueueLiveData = MutableLiveData<MutableSet<Tag>>().apply {
        value = mutableSetOf()
    }

    /**
     * Tags to return from the dialog when finished.
     */
    val tagQueue: Set<Tag> get() = tagQueueLiveData.value ?: emptySet()

    /**
     * Completely new tags to add to the tag collection when dialog is
     * confirmed.
     */
    private val newTagsToAdd = MutableLiveData<MutableSet<Tag>>().apply {
        value = mutableSetOf()
    }


    /**
     * Must be called when the dialog is confirmed.
     */
    fun onConfirmDialog() {
        tagCollection.addAll(newTagsToAdd.value ?: emptySet())
    }

    /**
     * Adds a tag to the queue of tags to return from the dialog when finished.
     *
     * @param tag - tag to add
     * @param isNewTag - Whether or not this tag needs to be added to the notes
     * collection.
     */
    fun addTagToQueue(tag: Tag, isNewTag: Boolean=false): Boolean {
        return (tagQueueLiveData.value?.add(tag) == true).also { success ->
            if (isNewTag) newTagsToAdd.value?.add(tag)

            // update LiveData
            if (success) tagQueueLiveData.value = tagQueueLiveData.value
        }
    }

    /**
     * Removes a tag from the queue of tags to return from then dialog when
     * finished.
     */
    fun removeTagFromQueue(tag: Tag): Boolean {
        return (tagQueueLiveData.value?.remove(tag) == true).also { success ->
            newTagsToAdd.value?.remove(tag)

            // update LiveData
            if (success) tagQueueLiveData.value = tagQueueLiveData.value
        }
    }
}