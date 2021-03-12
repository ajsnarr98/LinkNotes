package com.github.ajsnarr98.linkusers.data.fake

import com.github.ajsnarr98.linknotes.data.User
import com.github.ajsnarr98.linknotes.data.db.DAO
import com.github.ajsnarr98.linknotes.data.db.firestore.DBUser
import com.github.ajsnarr98.linknotes.data.db.firestore.FirestoreUserCollection
import com.github.ajsnarr98.linknotes.data.fake.FirestoreUsersDAOFake
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.*

/**
 * Test for using the user collection.
 */
@RunWith(RobolectricTestRunner::class)
class UserCollectionTest {

    private val sampleUsers = listOf<User>(
        User(
            id = "1",
        ),
        User(
            id = "2",
        ),
        User(
            id = "3",
        ),
    )

    private val emptyDAO = FirestoreUsersDAOFake()
    private val filledDAO = FirestoreUsersDAOFake().also { upsertSampleUsers(it) }

    /**
     * Gets a user in db format.
     */
    private fun dbUser(user: User): DBUser
        = DBUser.fromAppObject(user)

    private fun upsertSampleUsers(dao: DAO<DBUser>) {
        sampleUsers
            .map { user -> dbUser(user) }
            .forEach { user -> dao.upsert(user) }
    }

    @Test
    fun addTest() {

        val dao = emptyDAO
        val users = FirestoreUserCollection(dao)

        assertTrue("users is empty", users.isEmpty())
        var count = 0
        for (user in sampleUsers) {
            users.add(user)
            count++
            assertEquals("users has $count elements", count, users.size)
            assertEquals("db has $count elements", count, dao.collection.size)
        }

        // check if all samples are in collection and db
        for (user in sampleUsers) {
            assertTrue("is user ${user.id} in collection", users.contains(user))
            assertTrue("is user ${user.id} in db", dao.collection.contains(dbUser(user)))
        }
    }

    @Test
    fun addAllTest() {

        val dao = emptyDAO
        val users = FirestoreUserCollection(dao)

        assertTrue("users is empty", users.isEmpty())
        users.addAll(sampleUsers)
        assertEquals("users has ${sampleUsers.size} elements", sampleUsers.size, users.size)
        assertEquals("db has ${sampleUsers.size} elements", sampleUsers.size, dao.collection.size)

        // check if all samples are in collection and db
        for (user in sampleUsers) {
            assertTrue("is user ${user.id} in collection", users.contains(user))
            assertTrue("is user ${user.id} in db", dao.collection.contains(dbUser(user)))
        }
    }

    @Test
    fun clearTest() {

        val dao = filledDAO
        val users = FirestoreUserCollection(dao)

        assertEquals("users starts with ${sampleUsers.size} items in it", sampleUsers.size, users.size)
        users.clear()
        assertTrue("users is empty", users.isEmpty())
        assertTrue("db is empty", dao.collection.isEmpty())

        // make sure all samples are removed from collection and db
        for (user in sampleUsers) {
            assertFalse("is user ${user.id} not in collection", users.contains(user))
            assertFalse("is user ${user.id} not in db", dao.collection.contains(dbUser(user)))
        }
    }

    @Test
    fun initializeTest() {

        val dao = filledDAO
        val users = FirestoreUserCollection(dao)

        assertEquals("users starts with ${sampleUsers.size} items in it", sampleUsers.size, users.size)

        // check if all samples are in collection and db
        for (user in sampleUsers) {
            assertTrue("is user ${user.id} in collection", users.contains(user))
            assertTrue("is user ${user.id} in db", dao.collection.contains(dbUser(user)))
        }
    }

    @Test
    fun iteratorTest() {

        val dao = filledDAO
        val users = FirestoreUserCollection(dao)

        assertEquals("users starts with ${sampleUsers.size} items in it", sampleUsers.size, users.size)

        // check if all samples are in collection and db
        var count = 0
        for (user in users) {
            assertTrue("is user ${user.id} in sample collection", this.sampleUsers.contains(user))
            assertTrue("is user ${user.id} in db", dao.collection.contains(dbUser(user)))
            count++
        }
        assertEquals("Did iterator iterate ${sampleUsers.size} times", sampleUsers.size, count)
    }

    @Test
    fun removeTest() {

        val dao = filledDAO
        val users = FirestoreUserCollection(dao)

        assertEquals("users starts with ${sampleUsers.size} items in it", sampleUsers.size, users.size)
        var count = sampleUsers.size
        for (user in sampleUsers) {
            users.remove(user)
            count--
            assertEquals("users has $count elements", count, users.size)
            assertEquals("db has $count elements", count, dao.collection.size)
        }

        // make sure all samples are removed from collection and db
        for (user in sampleUsers) {
            assertFalse("is user ${user.id} not in collection", users.contains(user))
            assertFalse("is user ${user.id} not in db", dao.collection.contains(dbUser(user)))
        }
    }

    @Test
    fun removeAllTest() {

        val dao = filledDAO
        val users = FirestoreUserCollection(dao)

        assertEquals("users starts with ${sampleUsers.size} items in it", sampleUsers.size, users.size)
        users.removeAll(sampleUsers)
        assertTrue("users is empty", users.isEmpty())
        assertTrue("db is empty", dao.collection.isEmpty())

        // make sure all samples are removed from collection and db
        for (user in sampleUsers) {
            assertFalse("is user ${user.id} not in collection", users.contains(user))
            assertFalse("is user ${user.id} not in db", dao.collection.contains(dbUser(user)))
        }
    }

    @Test
    fun removeAllButOneTest() {

        val dao = filledDAO
        val users = FirestoreUserCollection(dao)

        assertEquals("users starts with ${sampleUsers.size} items in it", sampleUsers.size, users.size)
        val allButOneSample = LinkedList<User>(sampleUsers)
        val oneRemaining: User = allButOneSample.pop()
        users.removeAll(allButOneSample)
        assertEquals("users has one user", 1, users.size)
        assertEquals("db has one user", 1, dao.collection.size)

        // make sure all samples are removed from collection and db, except for the one left
        for (user in allButOneSample) {
            assertFalse("is user ${user.id} not in collection", users.contains(user))
            assertFalse("is user ${user.id} not in db", dao.collection.contains(dbUser(user)))
        }
        assertTrue("is user ${oneRemaining.id} left in collection", users.contains(oneRemaining))
        assertTrue("is user ${oneRemaining.id} left in db", dao.collection.contains(dbUser(oneRemaining)))
    }

    @Test
    fun retainAllButOneTest() {

        val dao = filledDAO
        val users = FirestoreUserCollection(dao)

        assertEquals("users starts with ${sampleUsers.size} items in it", sampleUsers.size, users.size)
        val allButOneSample = LinkedList<User>(sampleUsers)
        val oneRemoved: User = allButOneSample.pop()
        users.retainAll(allButOneSample)
        assertEquals("users has ${allButOneSample.size} users", allButOneSample.size, users.size)
        assertEquals("db has ${allButOneSample.size} users", allButOneSample.size, dao.collection.size)

        // make sure all samples are in from collection and db, except for the one left
        for (user in allButOneSample) {
            assertTrue("is user ${user.id} in collection", users.contains(user))
            assertTrue("is user ${user.id} in db", dao.collection.contains(dbUser(user)))
        }
        assertFalse("is user ${oneRemoved.id} not in collection", users.contains(oneRemoved))
        assertFalse("is user ${oneRemoved.id} not in db", dao.collection.contains(dbUser(oneRemoved)))
    }
}
