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
     * Existing tags that have not been added to the note to select from.
     */
    val selectableTagsLiveData = MutableLiveData<MutableSet<Tag>>().apply {
        value = mutableSetOf<Tag>().apply {
            addAll(tagCollection)
        }
    }

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
     * Only accepts existing tags.
     *
     * @param tags - tags to add
     */
    fun addTagsToQueue(tags: Collection<Tag>): Boolean {
        return (tagQueueLiveData.value?.addAll(tags) == true).also { success ->
            if (success) {
                if (selectableTagsLiveData.value?.removeAll(tags) == true) {
                    // update LiveData
                    selectableTagsLiveData.value = selectableTagsLiveData.value
                }

                // update LiveData
                tagQueueLiveData.value = tagQueueLiveData.value
            }
        }
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
            if (success) {
                if (isNewTag) {
                    newTagsToAdd.value?.add(tag)
                } else {
                    if (selectableTagsLiveData.value?.remove(tag) == true) {
                        // update LiveData
                        selectableTagsLiveData.value = selectableTagsLiveData.value
                    }
                }

                // update LiveData
                tagQueueLiveData.value = tagQueueLiveData.value
            }
        }
    }

    /**
     * Removes a tag from the queue of tags to return from then dialog when
     * finished.
     */
    fun removeTagsFromQueue(tags: Set<Tag>): Boolean {
        return (tagQueueLiveData.value?.removeAll(tags) == true).also { success ->
            if (success) {
                newTagsToAdd.value?.removeAll(tags)
                selectableTagsLiveData.value?.addAll(tags)

                // update LiveData
                tagQueueLiveData.value = tagQueueLiveData.value
                selectableTagsLiveData.value = selectableTagsLiveData.value
            }
        }
    }

    /**
     * Removes a tag from the queue of tags to return from then dialog when
     * finished.
     */
    fun removeTagFromQueue(tag: Tag): Boolean {
        return (tagQueueLiveData.value?.remove(tag) == true).also { success ->
            if (success) {
                newTagsToAdd.value?.remove(tag)
                selectableTagsLiveData.value?.add(tag)

                // update LiveData
                tagQueueLiveData.value = tagQueueLiveData.value
                selectableTagsLiveData.value = selectableTagsLiveData.value
            }
        }
    }
}