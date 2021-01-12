package com.github.ajsnarr98.linknotes.data.db

import androidx.lifecycle.LifecycleOwner
import com.github.ajsnarr98.linknotes.data.TagCollection
import com.google.firebase.firestore.DocumentChange
import timber.log.Timber
import java.lang.UnsupportedOperationException

class FirestoreTagCollection(private val dao: FirestoreDAO = FirestoreDAO.instance) : TagCollection() {

    init {
        // get tags from db
        dao.getAllTags(
            onSuccess = {tagTree -> this.safeAdd(tagTree); Timber.v("Received tagTree ${tagTree.topValue} from database")},
            onFailure = {err  -> Timber.e("Error getting note from db: $err")}
        )
    }

    /**
     * Add a listener for updating tags based on remote changes.
     */
    override fun onStart(owner: LifecycleOwner) {
        dao.addTagsChangeListener { snapshots, firebaseFirestoreException ->
            if (snapshots?.documentChanges == null) return@addTagsChangeListener

            Timber.i("Remote changes received in note collection")

            for (dc in snapshots.documentChanges) {
                val tagTree = dc.document.toObject(TagTree::class.java)
                when (dc.type) {
                    DocumentChange.Type.ADDED    -> this.safeAdd(tagTree)
                    DocumentChange.Type.REMOVED  -> this.safeRemove(tagTree)
                    DocumentChange.Type.MODIFIED -> this.safeAdd(tagTree)
                    else                         -> return@addTagsChangeListener
                }
            }
            // update based on current changes
            update()
        }
    }

    /**
     * Remove listener at end of activity.
     */
    override fun onStop(owner: LifecycleOwner) {
        dao.removeTagsChangeListener()
    }

    /**
     * Add all the tags in a tag tree without updating the database.
     */
    private fun safeAdd(elements: TagTree) {
        this.value.let { masterSet ->
            if (masterSet is TagTreesSet)
                masterSet.addTree(elements.toAppObject())
            else
                throw UnsupportedOperationException("Unknown set type")
        }
    }

    /**
     * Removes all the tags in the given tag tree without updating the database.
     */
    private fun safeRemove(elements: TagTree) {
        this.value.let { masterSet ->
            if (masterSet is TagTreesSet)
                masterSet.removeTree(elements.toAppObject())
            else
                throw UnsupportedOperationException("Unknown set type")
        }
    }

    override fun add(element: com.github.ajsnarr98.linknotes.data.Tag): Boolean {
        return this.value.let { masterSet ->
            if (masterSet is TagTreesSet)
                super.add(element).also {
                    val matchingRoot = masterSet.getMatchingRoot(element) ?: throw IllegalStateException("This should never get thrown")
                    dao.upsertTagTree(TagTree.fromAppObject(matchingRoot))
                }
            else
                throw UnsupportedOperationException("Unknown set type")
        }
    }

    override fun addAll(elements: Collection<com.github.ajsnarr98.linknotes.data.Tag>): Boolean {
        return this.value.let { masterSet ->
            if (masterSet is TagTreesSet)
                super.addAll(elements).also {
                    // get all roots in a set, in case some elements are from the same tree
                    val matchingRoots = mutableSetOf<TagTree>()
                    for (element in elements) {
                        val matchingRoot = masterSet.getMatchingRoot(element)
                            ?: throw IllegalStateException("This should never get thrown")
                        matchingRoots.add(TagTree.fromAppObject(matchingRoot))
                    }
                    for (root in matchingRoots) {
                        dao.upsertTagTree(root)
                    }
                }
            else
                throw UnsupportedOperationException("Unknown set type")
        }
    }

    override fun clear() {
        this.value.let { masterSet ->
            if (masterSet is TagTreesSet)
                dao.deleteTagTrees(masterSet.allTreeRoots().map { TagTree.fromAppObject(it) })
            else
                throw UnsupportedOperationException("Unknown set type")
        }
        super.clear()
    }

    override fun remove(element: com.github.ajsnarr98.linknotes.data.Tag): Boolean {
        return this.value.let { masterSet ->
            if (masterSet is TagTreesSet)
                super.remove(element).also {
                    val matchingRoot = masterSet.getMatchingRoot(element)
                    if (matchingRoot != null) {
                        // update existing tree
                        dao.upsertTagTree(TagTree.fromAppObject(matchingRoot))
                    } else {
                        // last part of tree was just removed entirely
                        dao.deleteTagTree(TagTree.fromAppObject(com.github.ajsnarr98.linknotes.data.TagTree.newTreeFrom(element)))
                    }
                }
            else
                throw UnsupportedOperationException("Unknown set type")
        }
    }

    override fun removeAll(elements: Collection<com.github.ajsnarr98.linknotes.data.Tag>): Boolean {
        return this.value.let { masterSet ->
            if (masterSet is TagTreesSet) {
                val matchingRoots = elements.mapNotNull { masterSet.getMatchingRoot(it) }.toSet()
                super.removeAll(elements).also {
                    val newMatchingRoots = elements.mapNotNull { masterSet.getMatchingRoot(it) }.toSet()
                    // update trees that still exist
                    newMatchingRoots.forEach { tree -> dao.upsertTagTree(TagTree.fromAppObject(tree)) }
                    // delete trees that no longer exist
                    val deletedTrees = matchingRoots.filter { it !in newMatchingRoots }
                        .map { tree -> TagTree.fromAppObject(tree) }
                    dao.deleteTagTrees(deletedTrees)
                }
            } else {
                throw UnsupportedOperationException("Unknown set type")
            }
        }
    }

    override fun retainAll(elements: Collection<com.github.ajsnarr98.linknotes.data.Tag>): Boolean {
        return this.value.let { masterSet ->
            val removeSet = this.filter { tag -> !elements.contains(tag) }
            if (masterSet is TagTreesSet) {
                val matchingRoots = removeSet.mapNotNull { masterSet.getMatchingRoot(it) }.toSet()
                super.retainAll(elements).also { successful ->
                    // only remove from db if retainAll was successful
                    if (successful) {
                        val newMatchingRoots =
                            elements.mapNotNull { masterSet.getMatchingRoot(it) }.toSet()
                        // update trees that still exist
                        newMatchingRoots.forEach { tree ->
                            dao.upsertTagTree(TagTree.fromAppObject(tree))
                        }
                        // delete trees that no longer exist
                        val deletedTrees = matchingRoots.filter { it !in newMatchingRoots }
                            .map { tree -> TagTree.fromAppObject(tree) }
                        dao.deleteTagTrees(deletedTrees)
                    }
                }
            } else {
                throw UnsupportedOperationException("Unknown set type")
            }
        }
    }
}