package com.github.ajsnarr98.linknotes.network

import com.github.ajsnarr98.linknotes.network.domain.Note
import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.network.domain.Tag
import com.github.ajsnarr98.linknotes.network.result.ErrorType
import com.github.ajsnarr98.linknotes.network.result.ResultStatus
import kotlinx.coroutines.flow.StateFlow

/**
 * Retrieves and manages notes from the DB.
 */
interface NotesRepository {

    enum class Action {
        DELETE, SAVE
    }

    data class PendingChange(
        val action: Action,
        val resultStatus: ResultStatus<Note, ErrorType>,
    )

    val isLoading: StateFlow<Boolean>

    /**
     * [notes] contains pending changes as well.
     */
    val notes: StateFlow<Set<Note>>

    val pending: StateFlow<List<PendingChange>>

    fun findByID(id: UUID): Note?

    /**
     * Refreshes list of notes. Also clears pending changes
     * without submitting them.
     *
     * Gives warning if pending changes exist.
     */
    suspend fun refresh(): ResultStatus<Unit, ErrorType>

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
    suspend fun discardPendingChanges(): ResultStatus<Unit, ErrorType>

    /**
     * Return true if there are pending changes for [note] already.
     */
    suspend fun arePendingChangesForNote(note: Note): ResultStatus<Boolean, ErrorType>

    /**
     * Tries to upsert given note, and assigns a new UUID to it if
     * successful. Success returns back the note with its UUID.
     *
     * If successful, updates [notes] respectively.
     */
    suspend fun saveNote(note: Note): ResultStatus<Note, ErrorType>

    /**
     * Tries to delete given note.
     *
     * If successful, updates [notes] respectively.
     */
    suspend fun deleteNote(note: Note): ResultStatus<Note, ErrorType>

    /**
     * Returns whether or not any notes contain the given tag. Check
     * includes pending changes to notes.
     */
    suspend fun containsTag(tag: Tag): ResultStatus<Boolean, ErrorType>
}
