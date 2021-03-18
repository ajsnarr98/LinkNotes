package com.github.ajsnarr98.linknotes.data

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
    private fun dbUser(user: User): DBUser = DBUser.fromAppObject(user)

    private fun upsertSampleUsers(dao: DAO<DBUser>) {
        sampleUsers
            .map { user -> dbUser(user) }
            .forEach { user -> dao.upsert(user) }
    }

    @Test
    fun addTest() {

        val dao = emptyDAO
        val users = FirestoreUserCollection(dao)

        assertTrue("dao is empty", dao.collection.isEmpty())
        var count = 0
        for (user in sampleUsers) {
            users.add(user)
            count++
            assertEquals("db has $count elements", count, dao.collection.size)
        }

        // check if all samples are in collection and db
        for (user in sampleUsers) {
            assertTrue("is user ${user.id} in db", dao.collection.contains(dbUser(user)))
        }
    }
}
