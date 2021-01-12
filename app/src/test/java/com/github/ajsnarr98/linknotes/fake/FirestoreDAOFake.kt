package com.github.ajsnarr98.linknotes.fake

import com.github.ajsnarr98.linknotes.data.db.FirestoreDAO
import com.github.ajsnarr98.linknotes.data.db.Note
import com.github.ajsnarr98.linknotes.data.db.TagTree
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import java.lang.Exception

class FirestoreDAOFake: FirestoreDAO {

    private val notes = mutableSetOf<Note>()
    private val tags = mutableSetOf<TagTree>()

    override fun getAllNotes(onSuccess: (Note) -> Unit, onFailure: (Exception) -> Unit) {
        for (note in notes) {
            onSuccess(note)
        }
    }

    override fun getAllTags(onSuccess: (TagTree) -> Unit, onFailure: (Exception) -> Unit) {
        for (tree in tags) {
            onSuccess(tree)
        }
    }

    override fun addNotesChangeListener(listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit) {
        // no op
    }

    override fun removeNotesChangeListener(): Boolean {
        // no op
        return true
    }

    override fun addTagsChangeListener(listener: (QuerySnapshot?, FirebaseFirestoreException?) -> Unit) {
        // no op
    }

    override fun removeTagsChangeListener(): Boolean {
        // no op
        return true
    }

    override fun upsertNote(note: Note): String {
        this.notes.add(note)
        return "0" // some UUID
    }

    override fun deleteNote(note: Note) {
        this.notes.remove(note)
    }

    override fun deleteNotes(notes: Collection<Note>) {
        this.notes.removeAll(notes)
    }

    override fun upsertTagTree(tags: TagTree): String {
        this.tags.add(tags)
        return "0" // some UUID
    }

    override fun deleteTagTree(tags: TagTree) {
        this.tags.remove(tags)
    }

    override fun deleteTagTrees(tags: Collection<TagTree>) {
        this.tags.removeAll(tags)
    }
}