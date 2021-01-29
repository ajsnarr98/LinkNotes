package com.github.ajsnarr98.linknotes

import com.github.ajsnarr98.linknotes.data.EntryList
import com.github.ajsnarr98.linknotes.data.Note
import com.github.ajsnarr98.linknotes.data.db.DAO
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreNoteCollection
import com.github.ajsnarr98.linknotes.fake.FirestoreNotesDAOFake
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant
import java.util.*

/**
 * Test for using the note collection.
 */
@RunWith(RobolectricTestRunner::class)
class NoteCollectionTest {

    private val sampleNotes = listOf<Note>(
        Note(
            id = "1",
            _type = Note.Type.DEFAULT,
            _name = "test1",
            dateCreated = Date.from(Instant.ofEpochSecond(300)),
            lastDateEdited = Date.from(Instant.ofEpochSecond(300)),
            nicknames = mutableListOf(),
            mainPicture = null,
            pictures = mutableListOf(),
            tags = mutableSetOf(),
            entries = EntryList.getEmpty(),
        ),
        Note(
            id = "2",
            _type = Note.Type.DEFAULT,
            _name = "test2",
            dateCreated = Date.from(Instant.ofEpochSecond(600)),
            lastDateEdited = Date.from(Instant.ofEpochSecond(900)),
            nicknames = mutableListOf(),
            mainPicture = null,
            pictures = mutableListOf(),
            tags = mutableSetOf(),
            entries = EntryList.getEmpty(),
        ),
        Note(
            id = "3",
            _type = Note.Type.DEFAULT,
            _name = "test3",
            dateCreated = Date.from(Instant.ofEpochSecond(900)),
            lastDateEdited = Date.from(Instant.ofEpochSecond(900)),
            nicknames = mutableListOf(),
            mainPicture = null,
            pictures = mutableListOf(),
            tags = mutableSetOf(),
            entries = EntryList.getEmpty(),
        ),
    )

    private val emptyDAO = FirestoreNotesDAOFake()
    private val filledDAO = FirestoreNotesDAOFake().also { upsertSampleNotes(it) }

    /**
     * Gets a note in db format.
     */
    private fun dbNote(note: Note): com.github.ajsnarr98.linknotes.data.db.firestore.Note
        = com.github.ajsnarr98.linknotes.data.db.firestore.Note.fromAppObject(note)

    private fun upsertSampleNotes(dao: DAO<com.github.ajsnarr98.linknotes.data.db.firestore.Note>) {
        sampleNotes
            .map { note -> dbNote(note) }
            .forEach { note -> dao.upsert(note) }
    }

    @Test
    fun addTest() {

        val dao = emptyDAO
        val notes = FirestoreNoteCollection(emptyDAO)

        assertTrue("notes is empty", notes.isEmpty())
        var count = 0
        for (note in sampleNotes) {
            notes.add(note)
            count++
            assertEquals("notes has $count elements", count, notes.size)
            assertEquals("db has $count elements", count, dao.collection.size)
        }

        // check if all samples are in collection and db
        for (note in sampleNotes) {
            assertTrue("is note ${note._name} in collection", notes.contains(note))
            assertTrue("is note ${note._name} in db", dao.collection.contains(dbNote(note)))
        }
    }

    @Test
    fun addAllTest() {

        val dao = emptyDAO
        val notes = FirestoreNoteCollection(emptyDAO)

        assertTrue("notes is empty", notes.isEmpty())
        notes.addAll(sampleNotes)
        assertEquals("notes has ${sampleNotes.size} elements", sampleNotes.size, notes.size)
        assertEquals("db has ${sampleNotes.size} elements", sampleNotes.size, dao.collection.size)

        // check if all samples are in collection and db
        for (note in sampleNotes) {
            assertTrue("is note ${note._name} in collection", notes.contains(note))
            assertTrue("is note ${note._name} in db", dao.collection.contains(dbNote(note)))
        }
    }

    @Test
    fun clearTest() {

        val dao = filledDAO
        val notes = FirestoreNoteCollection(filledDAO)

        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
        notes.clear()
        assertTrue("notes is empty", notes.isEmpty())
        assertTrue("db is empty", dao.collection.isEmpty())

        // make sure all samples are removed from collection and db
        for (note in sampleNotes) {
            assertFalse("is note ${note._name} not in collection", notes.contains(note))
            assertFalse("is note ${note._name} not in db", dao.collection.contains(dbNote(note)))
        }
    }

    @Test
    fun initializeTest() {

        val dao = filledDAO
        val notes = FirestoreNoteCollection(filledDAO)

        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)

        // check if all samples are in collection and db
        for (note in sampleNotes) {
            assertTrue("is note ${note._name} in collection", notes.contains(note))
            assertTrue("is note ${note._name} in db", dao.collection.contains(dbNote(note)))
        }
    }

    @Test
    fun iteratorTest() {

        val dao = filledDAO
        val notes = FirestoreNoteCollection(filledDAO)

        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)

        // check if all samples are in collection and db
        var count = 0
        for (note in notes) {
            assertTrue("is note ${note._name} in sample collection", this.sampleNotes.contains(note))
            assertTrue("is note ${note._name} in db", dao.collection.contains(dbNote(note)))
            count++
        }
        assertEquals("Did iterator iterate ${sampleNotes.size} times", sampleNotes.size, count)
    }

    @Test
    fun removeTest() {

        val dao = filledDAO
        val notes = FirestoreNoteCollection(filledDAO)

        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
        var count = sampleNotes.size
        for (note in sampleNotes) {
            notes.remove(note)
            count--
            assertEquals("notes has $count elements", count, notes.size)
            assertEquals("db has $count elements", count, dao.collection.size)
        }

        // make sure all samples are removed from collection and db
        for (note in sampleNotes) {
            assertFalse("is note ${note._name} not in collection", notes.contains(note))
            assertFalse("is note ${note._name} not in db", dao.collection.contains(dbNote(note)))
        }
    }

    @Test
    fun removeAllTest() {

        val dao = filledDAO
        val notes = FirestoreNoteCollection(filledDAO)

        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
        notes.removeAll(sampleNotes)
        assertTrue("notes is empty", notes.isEmpty())
        assertTrue("db is empty", dao.collection.isEmpty())

        // make sure all samples are removed from collection and db
        for (note in sampleNotes) {
            assertFalse("is note ${note._name} not in collection", notes.contains(note))
            assertFalse("is note ${note._name} not in db", dao.collection.contains(dbNote(note)))
        }
    }

    @Test
    fun removeAllButOneTest() {

        val dao = filledDAO
        val notes = FirestoreNoteCollection(filledDAO)

        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
        val allButOneSample = LinkedList<Note>(sampleNotes)
        val oneRemaining: Note = allButOneSample.pop()
        notes.removeAll(allButOneSample)
        assertEquals("notes has one note", 1, notes.size)
        assertEquals("db has one note", 1, dao.collection.size)

        // make sure all samples are removed from collection and db, except for the one left
        for (note in allButOneSample) {
            assertFalse("is note ${note._name} not in collection", notes.contains(note))
            assertFalse("is note ${note._name} not in db", dao.collection.contains(dbNote(note)))
        }
        assertTrue("is note ${oneRemaining._name} left in collection", notes.contains(oneRemaining))
        assertTrue("is note ${oneRemaining._name} left in db", dao.collection.contains(dbNote(oneRemaining)))
    }

    @Test
    fun retainAllButOneTest() {

        val dao = filledDAO
        val notes = FirestoreNoteCollection(filledDAO)

        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
        val allButOneSample = LinkedList<Note>(sampleNotes)
        val oneRemoved: Note = allButOneSample.pop()
        notes.retainAll(allButOneSample)
        assertEquals("notes has ${allButOneSample.size} notes", allButOneSample.size, notes.size)
        assertEquals("db has ${allButOneSample.size} notes", allButOneSample.size, dao.collection.size)

        // make sure all samples are in from collection and db, except for the one left
        for (note in allButOneSample) {
            assertTrue("is note ${note._name} in collection", notes.contains(note))
            assertTrue("is note ${note._name} in db", dao.collection.contains(dbNote(note)))
        }
        assertFalse("is note ${oneRemoved._name} not in collection", notes.contains(oneRemoved))
        assertFalse("is note ${oneRemoved._name} not in db", dao.collection.contains(dbNote(oneRemoved)))
    }
}
