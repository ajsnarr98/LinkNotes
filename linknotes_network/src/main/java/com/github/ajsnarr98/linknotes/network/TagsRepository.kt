package com.github.ajsnarr98.linknotes.network

import com.github.ajsnarr98.linknotes.network.domain.Tag
import com.github.ajsnarr98.linknotes.network.domain.TagSet
import kotlinx.coroutines.flow.StateFlow

interface TagsRepository {

    enum class Action {
        DELETE, ADD
    }

    data class PendingChange(
        val action: Action,
        val resultStatus: ResultStatus<Boolean>,
        val tag: Tag,
    )

    /**
     * [tags] contains pending changes as well.
     */
    val tags: StateFlow<TagSet>
    val isLoading: StateFlow<Boolean>
    val pending: StateFlow<List<PendingChange>>

    /**
     * Refreshes list of tags. Also clears pending changes
     * without submitting them.
     *
     * Gives warning if pending changes exist.
     */
    suspend fun refresh(): ResultStatus<Unit>

    /**
     * Gives tag if tag was added. False with a success result
     * means tag already exists.
     *
     * Updates list of tags respectively.
     */
    suspend fun addTag(tag: Tag): ResultStatus<Boolean>

    /**
     * Deletes a tag. This action will fail if any notes exist that contain
     * this tag.
     *
     * A success result with true means the tag was deleted, a success result
     * with false means the tag does not exist.
     *
     * Updates list of tags respectively.
     */
    suspend fun deleteTag(tag: Tag): ResultStatus<Boolean>

    /**
     * Gives a list of suggested tags based on the tag start. List is ordered
     * by best guess.
     */
    suspend fun getSearchSuggestions(searchStart: String): List<String>

    /**
     * Tries to submit pending changes in order. Stops at the first one that
     * fails. Result holds the new result status for the pending change that
     * failed, or null if all changes were submitted successfully.
     *
     * Updates list of pending changes and notes respectively.
     */
    suspend fun submitPendingChanges(): PendingChange?

    /**
     * Discard pending changes.
     */
    suspend fun discardPendingChanges(): ResultStatus<Unit>

    companion object {

        /**
         * Separator between tags and sub-tags. Ex: "classes.jmu"
         */
        const val SEPARATOR = "."

        fun isValidTag(tag: String): Boolean = reasonInvalidTag(tag).isEmpty()

        fun isValidTag(tag: Tag): Boolean = isValidTag(tag.text)

        /**
         * Gives the reason this tag is invalid, or "" if it is valid.
         */
        fun reasonInvalidTag(tag: Tag): String = reasonInvalidTag(tag.text)

        /**
         * Gives the reason this tag is invalid, or "" if it is valid.
         */
        fun reasonInvalidTag(tag: String): String {
            return when {
                tag.isEmpty() -> "Tag cannot be empty"
                tag.startsWith(SEPARATOR) -> "Invalid tag. Tag cannot start with $SEPARATOR"
                tag.endsWith(SEPARATOR) -> "Invalid tag. Tag cannot end with $SEPARATOR"
                else -> ""
            }
        }
    }
}