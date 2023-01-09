package com.github.ajsnarr98.linknotes.data.local

import android.app.Application
import android.content.Context
import com.github.ajsnarr98.linknotes.network.domain.Note
import com.github.ajsnarr98.linknotes.util.DelayedTask
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.StreamCorruptedException
import java.lang.IllegalStateException

/**
 * Persists unsaved changes.
 */
class UnsavedChangeStore(application: Application) {

    private val context: Context = application.applicationContext

    private var lastNoteChangeTime: Long = System.currentTimeMillis() - DEFAULT_NOTE_CHANGE_INTERVAL
    @Volatile private var waitingChanges: Note? = null // used to keep track of changes waiting to be persisted
    @Volatile private var noteSaveTask: DelayedTask<Boolean>? = null

    /**
     * Whether or not the note unsaved changes were updated within the given
     * interval.
     */
    private fun readyToUpdateUnsavedNoteChanges(): Boolean {
        return System.currentTimeMillis() >= (lastNoteChangeTime + DEFAULT_NOTE_CHANGE_INTERVAL)
    }

    /**
     * Clears saved "unsaved" changes for the given note.
     */
    fun clearNote(): Flow<Boolean> {
        return flow {
            onNoteChange()
            emit(context.deleteFile(UNSAVED_NOTE_FILE))
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Returns true when there were unsaved changes to a note last time the
     * app closed.
     */
    fun hasUnsavedChanges(): Flow<Boolean> {
        return getNote().map { note -> note != null }
    }

    /**
     * Returns a stored Note, or null if there is no stored note with
     * unsaved changes.
     */
    fun getNote(): Flow<Note?> {
        return flow {
            try {
                context.openFileInput(UNSAVED_NOTE_FILE).use { fileIn ->
                    ObjectInputStream(fileIn).use { objectIn ->
                        val result = objectIn.readObject() as? Note
                        onNoteChange()
                        emit(result)
                    }
                }
            } catch (e: FileNotFoundException) {
                // there is no note
                emit(null)
            } catch (e: StreamCorruptedException) {
                Timber.e(e)
                emit(null)
            } catch (e: IOException) {
                Timber.e(e)
                emit(null)
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * Writes this note to the UnsavedNote file, overwriting an existing note.
     *
     * Will only persist note immediately if no changes were made within the
     * last DEFAULT_NOTE_CHANGE_INTERVAL ms. Otherwise, waits until that time
     * and saves the most recent change then.
     */
    @ExperimentalCoroutinesApi
    fun persistNote(note: Note): Flow<Boolean> {
        return callbackFlow {
            synchronized(this) {
                waitingChanges = note
                if (noteSaveTask == null) {
                    val delay: Long = if (readyToUpdateUnsavedNoteChanges()) {
                        0L
                    } else {
                        System.currentTimeMillis() - (lastNoteChangeTime + DEFAULT_NOTE_CHANGE_INTERVAL)
                    }
                    noteSaveTask = DelayedTask(delay) {
                        this@UnsavedChangeStore.noteSaveTask = null
                        persistNoteImmediate()
                    }
                }
            }
            noteSaveTask?.addCallback { result ->
                offer(result)
                close()
            } ?: throw IllegalStateException("Failed to add callback")

            // try to start
            noteSaveTask?.start()
            // wait for callback to happen and close this channel
            awaitClose {  }

        }.flowOn(Dispatchers.IO)
    }

    /**
     * Saves waitingChanges.
     */
    private fun persistNoteImmediate(): Boolean {
        val note: Note
        synchronized(this) {
            // grab waiting changes
            note = waitingChanges ?: return false
            waitingChanges = null
        }
        return try {
            context.openFileOutput(UNSAVED_NOTE_FILE, Context.MODE_PRIVATE).use { fileOut ->
                ObjectOutputStream(fileOut).use { objectOut ->
                    objectOut.writeObject(note)
                }
            }
            true
        } catch (e: IOException) {
            Timber.e("Failed to write note to file")
            Timber.e(e)
            false
        }
    }

    private suspend fun onNoteChange() {
        lastNoteChangeTime = System.currentTimeMillis()
    }

    companion object {
        const val DEFAULT_NOTE_CHANGE_INTERVAL = 10000L // ms
        private const val UNSAVED_NOTE_FILE = "com.github.ajsnarr98.linknotes.data.local.UnsavedNote"
    }
}