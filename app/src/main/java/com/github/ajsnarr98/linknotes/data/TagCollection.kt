package com.github.ajsnarr98.linknotes.data

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LiveData
import timber.log.Timber

/**
 * Represents a collection of notes from the DB. Updates using livedata.
 *
 * DO NOT modify the value field of this class.
 */
abstract class TagCollection : LiveData<MutableSet<Tag>>(), MutableSet<Tag>, DefaultLifecycleObserver {

    init {
        if (this.value == null) {
            this.value = TagTreesSet()
        }

        Timber.i("Created tag collection")
    }

    companion object {

        /**
         * Separator between tags and sub-tags. Ex: "classes.jmu"
         */
        const val SEPARATOR = "."

        fun isValidTag(tag: String): Boolean = reasonInvalidTag(tag).isEmpty()

        fun isValidTag(tag: Tag): Boolean = isValidTag(tag.text)

        /**
         * Gives the reason this tag is invalid, or "" if it is valid.
         */
        fun reasonInvalidTag(tag: Tag): String = reasonInvalidTag(tag.text)

        /**
         * Gives the reason this tag is invalid, or "" if it is valid.
         */
        fun reasonInvalidTag(tag: String): String {
            return when {
                tag.isEmpty() -> "Tag cannot be empty"
                tag.startsWith(SEPARATOR) -> "Invalid tag. Tag cannot start with $SEPARATOR"
                tag.endsWith(SEPARATOR) -> "Invalid tag. Tag cannot end with $SEPARATOR"
                else -> ""
            }
        }

    }

    /**
     * Sets LiveData value to cause an event.
     */
    protected fun update() {
        this.value = this.value
    }

    /**
     * Get a tag for a given string.
     *
     * Return null if cannot find.
     */
    fun getMatch(tag: String): Tag? = this.value.let { value ->
        if (value is TagTreesSet)
            value.get(tag)
        else
            value?.find { it.text == tag }
    }

    // inherit set methods
    override val size: Int get() = this.value?.size ?: 0
    override fun contains(element: Tag): Boolean = this.value?.contains(element) ?: false
    override fun containsAll(elements: Collection<Tag>): Boolean = this.value?.containsAll(elements) ?: false
    override fun isEmpty(): Boolean = this.value?.isEmpty() ?: true
    override fun iterator(): MutableIterator<Tag> = this.value?.iterator() ?: mutableSetOf<Tag>().iterator()

    // inherit mutable set methods
    override fun add(element: Tag): Boolean = this.value?.add(element).also { update() } ?: false
    override fun addAll(elements: Collection<Tag>): Boolean = this.value?.addAll(elements).also { update() } ?: false // add all and return true if any were added
    override fun clear() { this.value?.clear().also { update() } }
    override fun remove(element: Tag): Boolean = this.value?.remove(element).also { update() } ?: false
    override fun removeAll(elements: Collection<Tag>): Boolean = this.value?.removeAll(elements).also { update() } ?: false
    override fun retainAll(elements: Collection<Tag>): Boolean = this.value?.retainAll(elements).also { update() } ?: false

    protected open class TagTreesSet : MutableSet<Tag> {

        private val tagTrees = mutableSetOf<TagTree>()

        /**
         * Get a matching tag for a given string.
         *
         * Return null if cannot find.
         */
        fun get(tag: String): Tag? = getMatchingRoot(tag)?.getAndReturn(tag)

        fun allTreeRoots(): Collection<TagTree> = tagTrees

        /**
         * Add a whole new tree to the set, or replace the tree with the same id.
         */
        fun addTree(root: TagTree) {
            tagTrees.remove(root) // remove old tree if it exists
            tagTrees.add(root)
        }

        /**
         * Remove a whole tree from the set, if it exists.
         */
        fun removeTree(root: TagTree) {
            tagTrees.remove(root)
        }

        /**
         * Get the root of the three for the given tag, or null if no matching
         * tree exists yet.
         */
        fun getMatchingRoot(element: Tag): TagTree? = getMatchingRoot(element.text)

        /**
         * Get the root of the three for the given tag, or null if no matching
         * tree exists yet.
         */
        private fun getMatchingRoot(element: String): TagTree? {
            val firstValue = TagTree.getSubtag(element).first
            return tagTrees.find { it.value == firstValue }
        }

        // inherit set methods
        override val size: Int get() = tagTrees.map { it.size }.sum()
        override fun contains(element: Tag): Boolean  = getMatchingRoot(element)?.contains(element) ?: false
        override fun containsAll(elements: Collection<Tag>): Boolean = elements.all { contains(it) }
        override fun isEmpty(): Boolean = tagTrees.isEmpty()
        override fun iterator(): MutableIterator<Tag> = object : MutableIterator<Tag> {

            val iterIter = tagTrees.iterator()
            var curIter: MutableIterator<Tag>? = if (iterIter.hasNext()) iterIter.next().iterator() else null

            override fun hasNext(): Boolean = curIter?.hasNext() == true || iterIter.hasNext()

            override fun next(): Tag {
                if (curIter?.hasNext() == false) curIter = iterIter.next().iterator()
                return curIter?.next() ?: throw NoSuchElementException()
            }

            override fun remove() {
                curIter?.remove() ?: throw NoSuchElementException()
            }
        }

        // inherit mutable set methods
        override fun add(element: Tag): Boolean = getMatchingRoot(element)?.add(element) ?: tagTrees.add(TagTree.newTreeFrom(element))
        override fun addAll(elements: Collection<Tag>): Boolean = elements.map { add(it) }.all { it } // add all and return true if any were added
        override fun clear() { tagTrees.clear() }
        override fun remove(element: Tag): Boolean {
            // return false if no root is found
            val root = getMatchingRoot(element) ?: return false
            // either remove an element from the root or remove the root itself (only if it has no children)
            val success = root.remove(element)
            if (!success && root.children.isEmpty()) {
                return tagTrees.remove(root)
            }
            return success
        }
        override fun removeAll(elements: Collection<Tag>): Boolean {
            // need to remove the elements in the correct order
            val elementsToKeep = this.filter { it !in elements }
            clear()
            return addAll(elementsToKeep)
        }
        override fun retainAll(elements: Collection<Tag>): Boolean {
            // cannot retail all if they aren't all here
            if (!containsAll(elements)) return false

            clear()
            return addAll(elements)
        }
    }
}
