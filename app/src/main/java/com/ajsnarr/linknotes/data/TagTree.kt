package com.ajsnarr.linknotes.data

import java.util.LinkedList

/**
 * Represents a node in a tree of strings, representing tags.
 *
 * For example "classes.jmu" would be the "jmu" tag as a leaf under "classes".
 */
data class TagTree(val value: String, val children: MutableSet<TagTree> = mutableSetOf()): MutableCollection<String> {
    companion object {
        /**
         * Separator between tags and sub-tags. Ex: "classes.jmu"
         */
        const val SEPARATOR = "."

        /**
         * Split "classes.jmu.gen-ed" into "classes" and "jmu.gen-ed".
         *
         * If no separators left to split off, return null as second value.
         */
        private fun getSubtag(tag: String): Pair<String, String?> {
            if (tag.isEmpty()) throw IllegalArgumentException("Tag cannot be empty.")
            val separatorInd = tag.indexOf(SEPARATOR)
            return when {
                // if separator not found
                separatorInd < 0              -> Pair(tag, null)
                // make sure there is still space after separator for subtag
                separatorInd < tag.length - 1 -> Pair(tag.substring(0, separatorInd), tag.substring(separatorInd + 1))
                // there were no other characters after separator
                else -> throw IllegalArgumentException("Invalid tag. Tag cannot end with $SEPARATOR")
            }
        }
    }

    val isLeaf: Boolean
        get() = children.isEmpty()

    override fun equals(other: Any?): Boolean {
        // ignore children
        return this === other
                || other is TagTree && this.value == other.value
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

    // --- Collection methods ---

    override val size: Int
        get() = if (isLeaf) 1 else children.size + 1

    override fun contains(element: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun containsAll(elements: Collection<String>): Boolean {
        return elements.all { contains(it) }
    }

    override fun isEmpty(): Boolean {
        // at the least, this node exists in the tree
        return false
    }

    override fun iterator(): Iterator<String> {
        //Build each tag using a depth first traversal.
        return object : Iterator<String> {

            val valStack: LinkedList<String> = LinkedList()
            val iterStack: LinkedList<Iterator<TagTree>> = LinkedList()

            init {
                // add initial values from this node
                valStack.push(this@TagTree.value)
                iterStack.push(this@TagTree.children.iterator())
            }

            var next: String? = fetchNext()

            /**
             * Fetch next value in iterator. Return null if no more values.
             */
            private fun fetchNext(): String? {

                // either get current iterator, or return null, because iterStack is empty
                var curIter: Iterator<TagTree>? = iterStack.peek()
                while (curIter?.hasNext() == false) {
                    valStack.pop()
                    iterStack.pop()
                    curIter = iterStack.peek()
                }

                if (curIter == null) return null

                // move down the tree
                val next = curIter.next()
                val lastValue = valStack.peek() ?: throw IllegalStateException("This should not happen")
                val nextValue = lastValue + SEPARATOR + next.value

                if (!next.isLeaf) {
                    iterStack.push(next.children.iterator())
                    valStack.push(nextValue)
                }

                // return value with value of the leaf at the end
                return nextValue
            }

            override fun hasNext(): Boolean = next != null

            override fun next(): String = next.also { fetchNext() } ?: throw NoSuchElementException()

        }
    }


    // --- Mutable collection methods ---

    /**
     * Add a tag string to the tree.
     */
    override fun add(tag: String): Boolean {
        if (tag.isEmpty()) return false // cannot add empty tag
        val split = getSubtag(tag)

        // get existing child or create new one for next part of tag
        val child: TagTree = children.firstOrNull { it.value == split.first }
            ?: TagTree(split.first).also { children.add(it) }

        child.add(split.second ?: "")
        return true
    }

    override fun addAll(elements: Collection<String>): Boolean {
        return elements.all { add(it) }
    }

    override fun clear() {
        this.children.clear()
    }

    override fun remove(element: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun removeAll(elements: Collection<String>): Boolean {
        TODO("Not yet implemented")
    }

    override fun retainAll(elements: Collection<String>): Boolean {
        TODO("Not yet implemented")
    }
}