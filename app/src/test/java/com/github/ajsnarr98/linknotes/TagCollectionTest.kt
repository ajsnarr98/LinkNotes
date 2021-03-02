package com.github.ajsnarr98.linknotes

import com.github.ajsnarr98.linknotes.data.Color
import com.github.ajsnarr98.linknotes.data.Tag
import com.github.ajsnarr98.linknotes.data.TagCollection
import com.github.ajsnarr98.linknotes.data.TagTree
import com.github.ajsnarr98.linknotes.data.db.DAO
import com.github.ajsnarr98.linknotes.data.db.firestore.DBTagTree
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreTagCollection
import com.github.ajsnarr98.linknotes.fake.FirestoreTagsDAOFake
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

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
    private val fullAddedTags = listOf<Tag>(
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

    /**
     * A list parallel to fullAddedTags where each value represents what unique
     * tag tree that sample tag is part of.
     */
    private val fullAddedTagsTagTreeIds = listOf<Int>(
        1,
        1,
        1,
        2,
        3,
        4,
        5,
        5,
        5,
        5,
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
    private fun dbTagTree(tagTree: TagTree): DBTagTree
        = DBTagTree.fromAppObject(tagTree)

    private fun upsertSampleTagTrees(dao: DAO<DBTagTree>) {
        sampleTagTrees
            .map { tagTree -> dbTagTree(tagTree) }
            .forEach { tagTree -> dao.upsert(tagTree) }
    }

    @Test
    fun addTest() {

        val dao = emptyDAO
        val tags = FirestoreTagCollection(dao)

        assertTrue("tags is empty", tags.isEmpty())
        val treeCount = mutableSetOf<Int>()
        for (i in 0.until(sampleTags.size)) {
            tags.add(sampleTags[i])
            treeCount.add(tagTreeIds[i])
            assertEquals("tags has ${tagCounts[i]} elements (iter $i)", tagCounts[i], tags.size)
            assertEquals("db has ${treeCount.size} elements (iter $i)", treeCount.size, dao.collection.size)
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
        val e = expected.children
        val a = actual?.children
        assertEquals("recursive check children size ($title):", expected.size, actual?.size)
        for (expChild in expected.children) {
            checkRecursive(expChild, actual?.children?.find { it.value == expChild.value })
        }
    }

    @Test
    fun addAllTest() {

        val dao = emptyDAO
        val tags = FirestoreTagCollection(dao)

        assertTrue("tags is empty", tags.isEmpty())
        tags.addAll(sampleTags)

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

    @Test
    fun initializeFullTest() {
        val dao = filledDAO
        val tags = FirestoreTagCollection(dao)

        // check if all samples are already added in collection and db
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

    @Test
    fun clearTest() {

        val dao = filledDAO
        val tags = FirestoreTagCollection(dao)

        assertEquals("tags starts with ${fullAddedTags.size} items in it", fullAddedTags.size, tags.size)
        tags.clear()
        assertTrue("tags is empty", tags.isEmpty())
        assertTrue("db is empty", dao.collection.isEmpty())

        // make sure all samples are removed from collection
        for (tag in fullAddedTags) {
            assertFalse("is tag ${tag.text} not in collection", tags.contains(tag))
        }
    }

    @Test
    fun iteratorTest() {

        val dao = filledDAO
        val tags = FirestoreTagCollection(dao)

        assertEquals("tags starts with ${fullAddedTags.size} items in it", fullAddedTags.size, tags.size)

        // check if all samples are in collection and db
        var count = 0
        for (tag in tags) {
            count++
        }
        assertEquals("Did iterator iterate ${fullAddedTags.size} times", fullAddedTags.size, count)
    }

    @Test
    fun removeTest() {

        val dao = filledDAO
        val tags = FirestoreTagCollection(dao)

        assertEquals("tags starts with ${fullAddedTags.size} items in it", fullAddedTags.size, tags.size)
        var count = fullAddedTags.size
        val treeCount = mutableSetOf<Int>()
        for (i in (fullAddedTags.size - 1) downTo 0) {
            tags.remove(fullAddedTags[i])
            count--
            treeCount.clear()
            repeat(i) { k -> treeCount.add(fullAddedTagsTagTreeIds[k]) }
            assertEquals("tags has $count elements (iter $i)", count, tags.size)
            assertEquals("db has ${treeCount.size} elements (iter $i)", treeCount.size, dao.collection.size)
        }

        // make sure all samples are removed from collection
        assertTrue("tags is empty", tags.isEmpty())
        assertTrue("db is empty", dao.collection.isEmpty())
        for (tag in fullAddedTags) {
            assertFalse("is tag ${tag.text} not in collection", tags.contains(tag))
        }
    }

    @Test
    fun removeAllTest() {

        val dao = filledDAO
        val tags = FirestoreTagCollection(dao)

        assertEquals("tags starts with ${fullAddedTags.size} items in it", fullAddedTags.size, tags.size)
        tags.removeAll(fullAddedTags)

        // make sure all samples are removed from collection
        assertTrue("tags is empty", tags.isEmpty())
        assertTrue("db is empty", dao.collection.isEmpty())
        for (tag in fullAddedTags) {
            assertFalse("is tag ${tag.text} not in collection", tags.contains(tag))
        }
    }

    @Test
    fun removeAllButOneTest() {

        val dao = filledDAO
        val tags = FirestoreTagCollection(dao)

        assertEquals("tags starts with ${fullAddedTags.size} items in it", fullAddedTags.size, tags.size)
        val allButOne = LinkedList<Tag>(fullAddedTags)
        val oneRemaining: Tag = allButOne.pop()
        tags.removeAll(allButOne)
        assertEquals("collection has correct number of tags left", 1, oneRemaining.text.split(TagCollection.SEPARATOR).size)
        assertEquals("db has one tag tree", 1, dao.collection.size)

        // make sure all samples are removed from collection and db, except for the one left
        for (tag in allButOne) {
            assertFalse("is tag ${tag.text} not in collection", tags.contains(tag))
        }
        assertTrue("is note ${oneRemaining.text} left in collection", tags.contains(oneRemaining))
    }

    @Test
    fun retainAllEverythingTest() {
        
        val dao = filledDAO
        val tags = FirestoreTagCollection(dao)

        assertEquals("tags starts with ${fullAddedTags.size} items in it", fullAddedTags.size, tags.size)
        tags.retainAll(fullAddedTags)
        assertEquals("collection has correct number of tags left", fullAddedTags.size, tags.size)
        assertEquals("db correct number of tag trees left", fullAddedTagsTagTreeIds.last(), dao.collection.size)

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
}
