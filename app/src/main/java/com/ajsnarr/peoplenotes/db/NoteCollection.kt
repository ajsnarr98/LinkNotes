package com.ajsnarr.peoplenotes.db

import androidx.lifecycle.LiveData

/**
 * Represents a collection of notes from the DB. Updates using livedata.
 */
abstract class NoteCollection : LiveData<MutableSet<Note>>(), MutableSet<Note>