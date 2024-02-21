package com.github.ajsnarr98.linknotes.network.storage.firestore

import com.github.ajsnarr98.linknotes.network.NotesRepository
import com.github.ajsnarr98.linknotes.network.result.ResultStatus
import com.github.ajsnarr98.linknotes.network.TagsRepository
import com.github.ajsnarr98.linknotes.network.storage.DAO
import com.github.ajsnarr98.linknotes.network.domain.Tag
import com.github.ajsnarr98.linknotes.network.domain.TagSet
import com.github.ajsnarr98.linknotes.network.result.ErrorType
import java.util.*
import kotlinx.coroutines.flow.MutableStateFlow

class FirestoreTagsRepository(
    private val dao: DAO<DBTag>,
    private val notesRepository: NotesRepository,
) : TagsRepository {

    // TODO save pending changes locally

    override val isLoading: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val tags: MutableStateFlow<TagSet> = MutableStateFlow(TagSet(emptyList()))
    override val pending: MutableStateFlow<List<TagsRepository.PendingChange>> = MutableStateFlow(
        emptyList()
    )

    override suspend fun refresh(): ResultStatus<Unit, ErrorType> {
        TODO("handle updated ResultStatus")
//        if (pending.value.isNotEmpty()) {
//            return ResultStatus.Error(Unit, ErrorType.LocalWarning.PendingChangesNotResolved)
//        }
//
//        isLoading.value = true
//        return try {
//            val allTags = dao.getAll().map { it.toAppObject() }
//            tags.value = TagSet(allTags)
//            discardPendingChanges()
//            ResultStatus.Success(Unit)
//        } catch (e: Exception) {
//            TODO()
//        } finally {
//            isLoading.value = false
//        }
    }

    override suspend fun addTag(tag: Tag): ResultStatus<Boolean, ErrorType> {
        TODO("handle updated ResultStatus")
//        if (tag in tags.value) return ResultStatus.Success(false)
//
//        isLoading.value = true
//        return try {
//            dao.upsert(DBTag.fromAppObject(tag))
//            tags.value = tags.value + tag
//            // remove all pending changes to tag, since call succeeded
//            pending.value = pending.value.filterNot { it.tag.text == tag.text }
//            ResultStatus.Success(true)
//        } catch (e: Exception) {
//            TODO(e.toString())
//        } finally {
//            isLoading.value = false
//        }
    }

    override suspend fun deleteTag(tag: Tag): ResultStatus<Boolean, ErrorType> {
        TODO("handle updated ResultStatus")
//        if (tag !in tags.value) return ResultStatus.Success(false)
//
//        isLoading.value = true
//        val error: ResultStatus.Error<Boolean>? = try {
//            val tagInUse = notesRepository.containsTag(tag)
//            if (tagInUse is ResultStatus.Success && tagInUse.result) {
//                ResultStatus.Error(false, ErrorType.LocalWarning.TagUsedInNote)
//            } else {
//                null
//            }
//        } catch (e: Throwable) {
//            TODO(e.toString())
//        }
//        if (error != null) {
//            isLoading.value = false
//            return error
//        }
//
//        return try {
//            dao.delete(DBTag.fromAppObject(tag))
//            // remove tag and pending changes to tag
//            tags.value = tags.value - tag
//            pending.value = pending.value.filterNot { it.tag.text == tag.text }
//            ResultStatus.Success(true)
//        } catch (e: Exception) {
//            TODO(e.toString())
//        } finally {
//            isLoading.value = false
//        }
    }

    override suspend fun getSearchSuggestions(searchStart: String): List<String> {
        TODO("handle updated ResultStatus")
    }

    override suspend fun submitPendingChanges(): TagsRepository.PendingChange? {
        TODO("handle updated ResultStatus")
//        val newPendingChanges = LinkedList(pending.value)
//
//        var next: TagsRepository.PendingChange
//        while (newPendingChanges.isNotEmpty()) {
//            next = newPendingChanges.removeFirst()
//
//            val newStatus: ResultStatus<Boolean> = when (next.action) {
//                TagsRepository.Action.ADD -> addTag(next.tag)
//                TagsRepository.Action.DELETE -> deleteTag(next.tag)
//            }
//            when (newStatus) {
//                is ResultStatus.Error -> {
//                    val newPendingChange = TagsRepository.PendingChange(
//                        action = next.action,
//                        resultStatus = newStatus,
//                        tag = next.tag,
//                    )
//                    newPendingChanges.addFirst(newPendingChange)
//                    pending.value = newPendingChanges
//                    return newPendingChange
//                }
//                is ResultStatus.Success -> { /* no-op */ }
//            }
//        }
//
//        return null
    }

    override suspend fun discardPendingChanges(): ResultStatus<Unit, ErrorType> {
        pending.value = emptyList()
        return ResultStatus.Success(Unit)
    }

    private fun addPendingChange(pendingChange: TagsRepository.PendingChange) {
        pending.value = pending.value + pendingChange
        tags.value = tags.value + pendingChange.tag
    }
}