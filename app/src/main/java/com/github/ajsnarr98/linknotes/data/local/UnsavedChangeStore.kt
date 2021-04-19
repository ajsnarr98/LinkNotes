package com.github.ajsnarr98.linknotes.data.local

import android.app.Application
import android.content.Context
import com.github.ajsnarr98.linknotes.data.Note
import timber.log.Timber
import java.io.FileNotFoundException
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.StreamCorruptedException

/**
 * Persists unsaved changes.
 */
class UnsavedChangeStore(application: Application) {

    val context: Context = application.applicationContext

    /**
     * Clears saved "unsaved" changes for the given note.
     *
     * TODO - make this use kotlin flow
     */
    fun clearNote(): Boolean {
        return context.deleteFile(UNSAVED_NOTE_FILE)
    }

    /**
     * Returns a stored Note, or null if there is no stored note with
     * unsaved changes.
     *
     * TODO - make this use kotlin flow
     */
    fun getNote(): Note? {
        return try {
            context.openFileInput(UNSAVED_NOTE_FILE).use { fileIn ->
                ObjectInputStream(fileIn).use { objectIn ->
                    objectIn.readObject() as? Note
                }
            }
        } catch (e: FileNotFoundException) {
            // there is no note
            null
        } catch (e: StreamCorruptedException) {
            Timber.e(e)
            null
        } catch (e: IOException) {
            Timber.e(e)
            null
        }
    }

    /**
     * Writes this note to the UnsavedNote file, overwriting an existing note.
     *
     * TODO - make this use kotlin flow
     */
    fun persistNote(note: Note): Boolean {
        try {
            context.openFileOutput(UNSAVED_NOTE_FILE, Context.MODE_PRIVATE).use { fileOut ->
                ObjectOutputStream(fileOut).use { objectOut ->
                    objectOut.writeObject(note)
                }
            }
        } catch (e: IOException) {
            Timber.e("Failed to write note to file")
            Timber.e(e)
            return false
        }
        return true
    }

    companion object {
        const val UNSAVED_NOTE_FILE = "com.github.ajsnarr98.linknotes.data.local.UnsavedNote"
    }
}