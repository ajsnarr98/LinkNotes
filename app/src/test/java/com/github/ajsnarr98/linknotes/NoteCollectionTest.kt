package com.github.ajsnarr98.linknotes

import com.github.ajsnarr98.linknotes.data.EntryList
import com.github.ajsnarr98.linknotes.data.Note
import com.github.ajsnarr98.linknotes.data.NoteCollection
import com.github.ajsnarr98.linknotes.data.db.FirestoreNoteCollection
import com.github.ajsnarr98.linknotes.fake.FirestoreDAOFake
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.util.*

/**
 * Test for using the note collection.
 */
class NoteCollectionTest {

    private lateinit var dao: FirestoreDAOFake
    private lateinit var notes: NoteCollection
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

    fun init() {
        dao = FirestoreDAOFake()
        notes = FirestoreNoteCollection(dao)
    }

    fun initWithNotes() {
        dao = FirestoreDAOFake()
        upsertSampleNotes()
        notes = FirestoreNoteCollection(dao)
    }

    /**
     * Gets a note in db format.
     */
    private fun dbNote(note: Note): com.github.ajsnarr98.linknotes.data.db.Note
        = com.github.ajsnarr98.linknotes.data.db.Note.fromAppObject(note)

    private fun upsertSampleNotes() {
        sampleNotes
            .map { note -> dbNote(note) }
            .forEach { note -> dao.upsertNote(note) }
    }

    @Test
    fun addTest() {
        init()
        assertTrue("notes is empty", notes.isEmpty())
        var count = 0
        for (note in sampleNotes) {
            notes.add(note)
            count++
            assertEquals("notes has $count elements", count, notes.size)
            assertEquals("db has $count elements", count, dao.notes.size)
        }

        // check if all samples are in collection and db
        for (note in sampleNotes) {
            assertTrue("is note ${note._name} in collection", this.notes.contains(note))
            assertTrue("is note ${note._name} in db", this.dao.notes.contains(dbNote(note)))
        }
    }

    @Test
    fun addAllTest() {
        init()
        assertTrue("notes is empty", notes.isEmpty())
        notes.addAll(sampleNotes)
        assertEquals("notes has ${sampleNotes.size} elements", sampleNotes.size, notes.size)
        assertEquals("db has ${sampleNotes.size} elements", sampleNotes.size, dao.notes.size)

        // check if all samples are in collection and db
        for (note in sampleNotes) {
            assertTrue("is note ${note._name} in collection", this.notes.contains(note))
            assertTrue("is note ${note._name} in db", this.dao.notes.contains(dbNote(note)))
        }
    }

    @Test
    fun clearTest() {
        initWithNotes()
        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
        notes.clear()
        assertTrue("notes is empty", notes.isEmpty())
        assertTrue("db is empty", dao.notes.isEmpty())

        // make sure all samples are removed from collection and db
        for (note in sampleNotes) {
            assertFalse("is note ${note._name} not in collection", this.notes.contains(note))
            assertFalse("is note ${note._name} not in db", this.dao.notes.contains(dbNote(note)))
        }
    }

    @Test
    fun initializeTest() {
        initWithNotes()
        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)

        // check if all samples are in collection and db
        for (note in sampleNotes) {
            assertTrue("is note ${note._name} in collection", this.notes.contains(note))
            assertTrue("is note ${note._name} in db", this.dao.notes.contains(dbNote(note)))
        }
    }

    @Test
    fun iteratorTest() {
        initWithNotes()
        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)

        // check if all samples are in collection and db
        var count = 0
        for (note in notes) {
            assertTrue("is note ${note._name} in sample collection", this.sampleNotes.contains(note))
            assertTrue("is note ${note._name} in db", this.dao.notes.contains(dbNote(note)))
            count++
        }
        assertEquals("Did iterator iterate ${sampleNotes.size} times", sampleNotes.size, count)
    }

    @Test
    fun removeTest() {
        initWithNotes()
        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
        var count = sampleNotes.size
        for (note in sampleNotes) {
            notes.remove(note)
            count--
            assertEquals("notes has $count elements", count, notes.size)
            assertEquals("db has $count elements", count, dao.notes.size)
        }

        // make sure all samples are removed from collection and db
        for (note in sampleNotes) {
            assertFalse("is note ${note._name} not in collection", this.notes.contains(note))
            assertFalse("is note ${note._name} not in db", this.dao.notes.contains(dbNote(note)))
        }
    }

    @Test
    fun removeAllTest() {
        initWithNotes()
        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
        notes.removeAll(sampleNotes)
        assertTrue("notes is empty", notes.isEmpty())
        assertTrue("db is empty", dao.notes.isEmpty())

        // make sure all samples are removed from collection and db
        for (note in sampleNotes) {
            assertFalse("is note ${note._name} not in collection", this.notes.contains(note))
            assertFalse("is note ${note._name} not in db", this.dao.notes.contains(dbNote(note)))
        }
    }

    @Test
    fun removeAllButOneTest() {
        initWithNotes()
        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
        val allButOneSample = LinkedList<Note>(sampleNotes)
        val oneRemaining: Note = allButOneSample.pop()
        notes.removeAll(allButOneSample)
        assertEquals("notes has one note", 1, notes.size)
        assertEquals("db has one note", 1, dao.notes.size)

        // make sure all samples are removed from collection and db, except for the one left
        for (note in allButOneSample) {
            assertFalse("is note ${note._name} not in collection", this.notes.contains(note))
            assertFalse("is note ${note._name} not in db", this.dao.notes.contains(dbNote(note)))
        }
        assertTrue("is note ${oneRemaining._name} left in collection", this.notes.contains(oneRemaining))
        assertTrue("is note ${oneRemaining._name} left in db", this.dao.notes.contains(dbNote(oneRemaining)))
    }

    @Test
    fun retainAllButOneTest() {
        initWithNotes()
        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
        val allButOneSample = LinkedList<Note>(sampleNotes)
        val oneRemoved: Note = allButOneSample.pop()
        notes.retainAll(allButOneSample)
        assertEquals("notes has ${allButOneSample.size} notes", allButOneSample.size, notes.size)
        assertEquals("db has ${allButOneSample.size} notes", allButOneSample.size, dao.notes.size)

        // make sure all samples are in from collection and db, except for the one left
        for (note in allButOneSample) {
            assertTrue("is note ${note._name} in collection", this.notes.contains(note))
            assertTrue("is note ${note._name} in db", this.dao.notes.contains(dbNote(note)))
        }
        assertFalse("is note ${oneRemoved._name} not in collection", this.notes.contains(oneRemoved))
        assertFalse("is note ${oneRemoved._name} not in db", this.dao.notes.contains(dbNote(oneRemoved)))
    }
}
