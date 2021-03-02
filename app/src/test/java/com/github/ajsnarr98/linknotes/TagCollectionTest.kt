package com.github.ajsnarr98.linknotes

import com.github.ajsnarr98.linknotes.data.Color
import com.github.ajsnarr98.linknotes.data.Tag
import com.github.ajsnarr98.linknotes.data.TagTree
import com.github.ajsnarr98.linknotes.data.db.DAO
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreTagCollection
import com.github.ajsnarr98.linknotes.fake.FirestoreTagsDAOFake
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*
import kotlin.math.exp

/**
 * Test for using the note collection.
 */
@RunWith(RobolectricTestRunner::class)
class TagCollectionTest {

    private val BLUE = Color(0,0,255)

    private val sampleTags = listOf<Tag>(
        Tag("places.charlottesville", BLUE),
        Tag("places.harrisonburg", BLUE),
        Tag("pets", BLUE),
        Tag("pineapple", BLUE),
        Tag("apple", BLUE),
        Tag("abc.123", BLUE),
        Tag("abc.abc", BLUE),
        Tag("abc.123.do.re.mi.so", BLUE),
        Tag("abc.123.do.re.mi.fa", BLUE),
    )

    /**
     * All tags, including tags created when adding the list of sampleTags.
     */
    private val fullAddedTags = setOf<Tag>(
        Tag("places"),
        Tag("places.charlottesville", BLUE),
        Tag("places.harrisonburg", BLUE),
        Tag("pets", BLUE),
        Tag("pineapple", BLUE),
        Tag("apple", BLUE),
        Tag("abc"),
        Tag("abc.123", BLUE),
        Tag("abc.abc", BLUE),
        Tag("abc.123.do"),
        Tag("abc.123.do.re"),
        Tag("abc.123.do.re.mi"),
        Tag("abc.123.do.re.mi.so", BLUE),
        Tag("abc.123.do.re.mi.fa", BLUE),
    )

    /**
     * A list parallel to sampleTags where each value represents how many tags
     * should be in the full collection at that point, assuming sampleTags are
     * added in order.
     */
    private val tagCounts = listOf<Int>(
        2,
        3,
        4,
        5,
        6,
        8,
        9,
        13,
        14,
    )

    /**
     * A list parallel to sampleTags where each value represents what unique
     * tag tree that sample tag is part of.
     */
    private val tagTreeIds = listOf<Int>(
        1,
        1,
        2,
        3,
        4,
        5,
        5,
        5,
        5,
    )

    /** expected tag trees given sample tags */
    private val sampleTagTrees = listOf<TagTree>(
        TagTree(
            "places",
            BLUE,
            children = mutableSetOf(
                TagTree("charlottesville", BLUE),
                TagTree("harrisonburg", BLUE),
            ),
        ),
        TagTree("pets", BLUE),
        TagTree("pineapple", BLUE),
        TagTree("apple", BLUE),
        TagTree(
            "abc",
            BLUE,
            children = mutableSetOf(
                TagTree("abc", BLUE),
                TagTree("123",
                    BLUE,
                    children = mutableSetOf(
                        TagTree("do",
                            BLUE,
                            children = mutableSetOf(
                                TagTree("re",
                                    BLUE,
                                    children = mutableSetOf(
                                        TagTree("mi",
                                            BLUE,
                                            children = mutableSetOf(
                                                TagTree("so", BLUE),
                                                TagTree("fa", BLUE),
                                            ),
                                        ),
                                    ),
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        ),
    )

    private val emptyDAO = FirestoreTagsDAOFake()
    private val filledDAO = FirestoreTagsDAOFake().also { upsertSampleTagTrees(it) }

    /**
     * Gets a tag tree in db format.
     */
    private fun dbTagTree(tagTree: TagTree): com.github.ajsnarr98.linknotes.data.db.firestore.TagTree
        = com.github.ajsnarr98.linknotes.data.db.firestore.TagTree.fromAppObject(tagTree)

    private fun upsertSampleTagTrees(dao: DAO<com.github.ajsnarr98.linknotes.data.db.firestore.TagTree>) {
        sampleTagTrees
            .map { tagTree -> dbTagTree(tagTree) }
            .forEach { tagTree -> dao.upsert(tagTree) }
    }

    @Test
    fun addTest() {

        val dao = emptyDAO
        val tags = FirestoreTagCollection(emptyDAO)

        assertTrue("tags is empty", tags.isEmpty())
        val treeCount = mutableSetOf<Int>()
        for (i in 0.until(sampleTags.size)) {
            tags.add(sampleTags[i])
            treeCount.add(tagTreeIds[i])
            assertEquals("tags has ${tagCounts[i]} elements", tagCounts[i], tags.size)
            assertEquals("db has ${treeCount.size} elements", treeCount.size, dao.collection.size)
        }

        // check if all samples are in collection and db
        assertEquals("is tag collection same size as expected list", fullAddedTags.size, tags.size)
        assertEquals("is dao same size as expected list", sampleTagTrees.size, dao.collection.size)
        for (tag in fullAddedTags) {
            assertTrue("is tag ${tag.text} in collection", tags.contains(tag))
        }
        for (tagTree in sampleTagTrees) {
            assertTrue(
                "is tag tree ${tagTree.value} in db",
                dao.collection.contains(dbTagTree(tagTree))
            )
            checkRecursive(tagTree, dao.collection.find { it.toAppObject().value == tagTree.value }?.toAppObject())
        }
    }

    /**
     * Recursively check equality for two trees.
     */
    private fun checkRecursive(expected: TagTree, actual: TagTree?) {
        val title = expected.value
        assertNotNull("check recursive not null ($title)", actual)
        assertEquals("recursive check ($title):", expected.value, actual?.value)
        assertEquals("recursive check children size ($title):", expected.size, actual?.size)
        for (expChild in expected.children) {
            checkRecursive(expChild, actual?.children?.find { it.value == expChild.value })
        }
    }

//    @Test
//    fun addAllTest() {
//
//        val dao = emptyDAO
//        val notes = FirestoreNoteCollection(emptyDAO)
//
//        assertTrue("notes is empty", notes.isEmpty())
//        notes.addAll(sampleNotes)
//        assertEquals("notes has ${sampleNotes.size} elements", sampleNotes.size, notes.size)
//        assertEquals("db has ${sampleNotes.size} elements", sampleNotes.size, dao.collection.size)
//
//        // check if all samples are in collection and db
//        for (note in sampleNotes) {
//            assertTrue("is note ${note._name} in collection", notes.contains(note))
//            assertTrue("is note ${note._name} in db", dao.collection.contains(dbNote(note)))
//        }
//    }
//
//    @Test
//    fun clearTest() {
//
//        val dao = filledDAO
//        val notes = FirestoreNoteCollection(filledDAO)
//
//        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
//        notes.clear()
//        assertTrue("notes is empty", notes.isEmpty())
//        assertTrue("db is empty", dao.collection.isEmpty())
//
//        // make sure all samples are removed from collection and db
//        for (note in sampleNotes) {
//            assertFalse("is note ${note._name} not in collection", notes.contains(note))
//            assertFalse("is note ${note._name} not in db", dao.collection.contains(dbNote(note)))
//        }
//    }
//
//    @Test
//    fun initializeTest() {
//
//        val dao = filledDAO
//        val notes = FirestoreNoteCollection(filledDAO)
//
//        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
//
//        // check if all samples are in collection and db
//        for (note in sampleNotes) {
//            assertTrue("is note ${note._name} in collection", notes.contains(note))
//            assertTrue("is note ${note._name} in db", dao.collection.contains(dbNote(note)))
//        }
//    }
//
//    @Test
//    fun iteratorTest() {
//
//        val dao = filledDAO
//        val notes = FirestoreNoteCollection(filledDAO)
//
//        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
//
//        // check if all samples are in collection and db
//        var count = 0
//        for (note in notes) {
//            assertTrue("is note ${note._name} in sample collection", this.sampleNotes.contains(note))
//            assertTrue("is note ${note._name} in db", dao.collection.contains(dbNote(note)))
//            count++
//        }
//        assertEquals("Did iterator iterate ${sampleNotes.size} times", sampleNotes.size, count)
//    }
//
//    @Test
//    fun removeTest() {
//
//        val dao = filledDAO
//        val notes = FirestoreNoteCollection(filledDAO)
//
//        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
//        var count = sampleNotes.size
//        for (note in sampleNotes) {
//            notes.remove(note)
//            count--
//            assertEquals("notes has $count elements", count, notes.size)
//            assertEquals("db has $count elements", count, dao.collection.size)
//        }
//
//        // make sure all samples are removed from collection and db
//        for (note in sampleNotes) {
//            assertFalse("is note ${note._name} not in collection", notes.contains(note))
//            assertFalse("is note ${note._name} not in db", dao.collection.contains(dbNote(note)))
//        }
//    }
//
//    @Test
//    fun removeAllTest() {
//
//        val dao = filledDAO
//        val notes = FirestoreNoteCollection(filledDAO)
//
//        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
//        notes.removeAll(sampleNotes)
//        assertTrue("notes is empty", notes.isEmpty())
//        assertTrue("db is empty", dao.collection.isEmpty())
//
//        // make sure all samples are removed from collection and db
//        for (note in sampleNotes) {
//            assertFalse("is note ${note._name} not in collection", notes.contains(note))
//            assertFalse("is note ${note._name} not in db", dao.collection.contains(dbNote(note)))
//        }
//    }
//
//    @Test
//    fun removeAllButOneTest() {
//
//        val dao = filledDAO
//        val notes = FirestoreNoteCollection(filledDAO)
//
//        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
//        val allButOneSample = LinkedList<Note>(sampleNotes)
//        val oneRemaining: Note = allButOneSample.pop()
//        notes.removeAll(allButOneSample)
//        assertEquals("notes has one note", 1, notes.size)
//        assertEquals("db has one note", 1, dao.collection.size)
//
//        // make sure all samples are removed from collection and db, except for the one left
//        for (note in allButOneSample) {
//            assertFalse("is note ${note._name} not in collection", notes.contains(note))
//            assertFalse("is note ${note._name} not in db", dao.collection.contains(dbNote(note)))
//        }
//        assertTrue("is note ${oneRemaining._name} left in collection", notes.contains(oneRemaining))
//        assertTrue("is note ${oneRemaining._name} left in db", dao.collection.contains(dbNote(oneRemaining)))
//    }
//
//    @Test
//    fun retainAllButOneTest() {
//
//        val dao = filledDAO
//        val notes = FirestoreNoteCollection(filledDAO)
//
//        assertEquals("notes starts with ${sampleNotes.size} items in it", sampleNotes.size, notes.size)
//        val allButOneSample = LinkedList<Note>(sampleNotes)
//        val oneRemoved: Note = allButOneSample.pop()
//        notes.retainAll(allButOneSample)
//        assertEquals("notes has ${allButOneSample.size} notes", allButOneSample.size, notes.size)
//        assertEquals("db has ${allButOneSample.size} notes", allButOneSample.size, dao.collection.size)
//
//        // make sure all samples are in from collection and db, except for the one left
//        for (note in allButOneSample) {
//            assertTrue("is note ${note._name} in collection", notes.contains(note))
//            assertTrue("is note ${note._name} in db", dao.collection.contains(dbNote(note)))
//        }
//        assertFalse("is note ${oneRemoved._name} not in collection", notes.contains(oneRemoved))
//        assertFalse("is note ${oneRemoved._name} not in db", dao.collection.contains(dbNote(oneRemoved)))
//    }
}
