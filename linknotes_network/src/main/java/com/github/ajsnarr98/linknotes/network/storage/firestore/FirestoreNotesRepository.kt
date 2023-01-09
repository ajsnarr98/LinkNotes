package com.github.ajsnarr98.linknotes.network.storage.firestore

import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.network.NotesRepository
import com.github.ajsnarr98.linknotes.network.ResultStatus
import com.github.ajsnarr98.linknotes.network.storage.DAO
import com.github.ajsnarr98.linknotes.network.domain.Note
import com.github.ajsnarr98.linknotes.network.domain.Tag
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow

class FirestoreNotesRepository(
    private val dao: DAO<DBNote>,
) : NotesRepository {

    // TODO add logging

    override val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val notes: MutableStateFlow<Set<Note>> = MutableStateFlow(emptySet())

    override val pending: MutableStateFlow<List<NotesRepository.PendingChange>> = MutableStateFlow(
        emptyList()
    )

    override fun findByID(id: UUID): Note? {
        return notes.value.firstOrNull { note -> note.id == id }
    }

    override suspend fun refresh(): ResultStatus<Unit> {
        if (pending.value.isNotEmpty()) {
            return ResultStatus.Error(Unit, ResultStatus.ErrorType.LocalWarning.PendingChangesNotResolved)
        }

        isLoading.value = true
        return try {
            val allNotes = dao.getAll().map { it.toAppObject() }.toSet()
            notes.value = allNotes
            discardPendingChanges()
            ResultStatus.Success(Unit)
        } catch (e: Exception) {
            TODO()
        } finally {
            isLoading.value = false
        }
    }

    override suspend fun submitPendingChanges(): NotesRepository.PendingChange? {
        val newPendingChanges = LinkedList(pending.value)

        var next: NotesRepository.PendingChange
        while (newPendingChanges.isNotEmpty()) {
            next = newPendingChanges.removeFirst()

            val newStatus = when (next.action) {
                NotesRepository.Action.SAVE -> saveNote(next.resultStatus.result)
                NotesRepository.Action.DELETE -> deleteNote(next.resultStatus.result)
            }
            when (newStatus) {
                is ResultStatus.Error -> {
                    val newPendingChange = NotesRepository.PendingChange(
                        action = next.action,
                        resultStatus = newStatus,
                    )
                    newPendingChanges.addFirst(newPendingChange)
                    pending.value = newPendingChanges
                    return newPendingChange
                }
                is ResultStatus.Success -> { /* no-op */ }
            }
        }

        return null
    }

    override suspend fun discardPendingChanges(): ResultStatus<Unit> {
        pending.value = emptyList()
        return ResultStatus.Success(Unit)
    }

    override suspend fun arePendingChangesForNote(note: Note): ResultStatus<Boolean> {
        return ResultStatus.Success(pending.value.any { pendingChange ->
            val other = when (val resultStatus = pendingChange.resultStatus) {
                is ResultStatus.Success -> resultStatus.result
                is ResultStatus.Error -> resultStatus.result
            }
            note.id == other.id
        })
    }

    override suspend fun saveNote(note: Note): ResultStatus<Note> {
        isLoading.value = true
        return try {
            val upsertedNoteId = dao.upsert(DBNote.fromAppObject(note))
            val upsertedNote = if (note.isNewNote()) note.edited(id = upsertedNoteId) else note

            notes.value = (notes.value - note) + upsertedNote

            // remove all pending changes to note, since call succeeded
            pending.value = pending.value.filterNot { it.resultStatus.result.id == note.id }

            ResultStatus.Success(upsertedNote)
        } catch (e: Exception) {
            TODO(e.toString())
        } finally {
            isLoading.value = false
        }
    }

    override suspend fun deleteNote(note: Note): ResultStatus<Note> {
        isLoading.value = true
        return try {
            val deletedNote = dao.delete(DBNote.fromAppObject(note)).toAppObject()

            // remove note and all pending changes to note
            notes.value = notes.value - deletedNote
            pending.value = pending.value.filterNot { it.resultStatus.result.id == note.id }
            ResultStatus.Success(deletedNote)
        } catch (e: Exception) {
            TODO(e.toString())
        } finally {
            isLoading.value = false
        }
    }

    override suspend fun containsTag(tag: Tag): ResultStatus<Boolean> {
        return ResultStatus.Success(
            notes.value.any { note -> note.containsTag(tag) }
        )
    }

    private fun addPendingChange(pendingChange: NotesRepository.PendingChange) {
        pending.value = pending.value + pendingChange
        notes.value = notes.value + when (val resultStatus = pendingChange.resultStatus) {
            is ResultStatus.Success -> resultStatus.result
            is ResultStatus.Error -> resultStatus.result
        }
    }
}
