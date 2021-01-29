package com.github.ajsnarr98.linknotes.fake

import com.github.ajsnarr98.linknotes.data.Note
import com.github.ajsnarr98.linknotes.data.db.NotesDAO

class FirestoreNotesDAOFake : AbstractFirestoreDAOFake<Note, com.github.ajsnarr98.linknotes.data.db.Note>(), NotesDAO