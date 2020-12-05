package com.ajsnarr.linknotes.data

import java.lang.UnsupportedOperationException
import java.util.LinkedList

/**
 * Represents a node in a tree of strings, representing tags.
 *
 * For example "classes.jmu" would be the "jmu" tag as a leaf under "classes".
 */
data class TagTree(val value: String, val color: Color, val children: MutableSet<TagTree> = mutableSetOf()): MutableSet<Tag>, AppDataObject {

    companion object {

        /**
         * Split "classes.jmu.gen-ed" into "classes" and "jmu.gen-ed".
         *
         * If no separators left to split off, return null as second value.
         */
        fun getSubtag(tag: String): Pair<String, String?> {
            if (tag.isEmpty()) throw IllegalArgumentException("Tag cannot be empty.")
            val separatorInd = tag.indexOf(TagCollection.SEPARATOR)
            return when {
                // if separator not found
                separatorInd < 0              -> Pair(tag, null)
                // make sure there is still space after separator for subtag
                separatorInd < tag.length - 1 -> Pair(tag.substring(0, separatorInd), tag.substring(separatorInd + 1))
                // there were no other characters after separator
                else -> throw IllegalArgumentException("Invalid tag. Tag cannot end with ${TagCollection.SEPARATOR}")
            }
        }

        /**
         * Creates a new tree root with the given tag added.
         */
        fun newTreeFrom(tag: Tag): TagTree {
            if (!TagCollection.isValidTag(tag)) throw IllegalArgumentException(TagCollection.reasonInvalidTag(tag))
            val nodes = tag.text.split(TagCollection.SEPARATOR)

            return if (nodes.size == 1) {
                TagTree(tag.text, tag.color)
            } else {
                val root = TagTree(nodes.first(), Color.randomTagColor())
                var current = root
                for (i in 1 until nodes.size - 1) {
                    val next = TagTree(nodes[i], Color.randomTagColor())
                    current.children.add(next)
                    current = next
                }
                current.children.add(TagTree(nodes.last(), tag.color))
                root
            }
        }
    }

    val isLeaf: Boolean
        get() = children.isEmpty()

    override fun equals(other: Any?): Boolean {
        // ignore children and color
        return this === other
                || other is TagTree && this.value == other.value
    }

    override fun hashCode(): Int {
        // ignore children and color
        return value.hashCode()
    }

    // --- Collection methods ---

    override val size: Int
        get() = if (isLeaf) 1 else children.size + 1

    /**
     * Whether or not this tree contains the given element.
     *
     * @param element - if this node's value is "foo", and you want to know if
     *                  this tree contains "foo.bar" element would be the full
     *                  string "foo.bar"
     */
    override fun contains(element: Tag): Boolean = contains(element.text)

    /**
     * Whether or not this tree contains the given element.
     *
     * @param element - if this node's value is "foo", and you want to know if
     *                  this tree contains "foo.bar" element would be the full
     *                  string "foo.bar"
     */
    private fun contains(element: String): Boolean = getAndReturn(element) != null

    fun getAndReturn(element: String): Tag? = getAndReturn(element, element)

    private fun getAndReturn(fullTag: String, partialTag: String): Tag? {
        if (partialTag.isEmpty()) return null // cannot have empty tag
        if (partialTag == this.value) return Tag(fullTag, color) // at a matching point
        val split = getSubtag(partialTag)

        if (split.first != this.value || split.second == null) return null

        // get next matching child, or return false if not found
        val childValue = getSubtag(split.second!!).first
        val child: TagTree = children.firstOrNull { it.value == childValue }
            ?: return null

        return child.getAndReturn(fullTag, split.second ?: "")
    }

    override fun containsAll(elements: Collection<Tag>): Boolean = elements.all { contains(it) }

    override fun isEmpty(): Boolean {
        // at the least, this node exists in the tree
        return false
    }

    override fun iterator(): MutableIterator<Tag> {
        //Build each tag using a depth first traversal.
        return object : MutableIterator<Tag> {

            val valStack: LinkedList<String> = LinkedList()
            val iterStack: LinkedList<Iterator<TagTree>> = LinkedList()

            init {
                // add initial values from this node
                valStack.push(this@TagTree.value)
                iterStack.push(this@TagTree.children.iterator())
            }

            var next: Tag? = fetchNext()

            /**
             * Fetch next value in iterator. Return null if no more values.
             */
            private fun fetchNext(): Tag? {

                // either get current iterator, return root, or return null, because iterStack is empty
                var curIter: Iterator<TagTree>? = iterStack.peek()
                while (curIter?.hasNext() == false && valStack.size > 1) {
                    valStack.pop()
                    iterStack.pop()
                    curIter = iterStack.peek()
                }

                // return root
                if (valStack.size == 1) return Tag(valStack.pop(), this@TagTree.color).also {
                    iterStack.pop()
                }

                if (curIter == null) return null

                // move down the tree
                val next = curIter.next()
                val lastValue = valStack.peek() ?: throw IllegalStateException("This should not happen")
                val nextValue = lastValue + TagCollection.SEPARATOR + next.value

                if (!next.isLeaf) {
                    iterStack.push(next.children.iterator())
                    valStack.push(nextValue)
                }

                // return value with value of the leaf at the end
                return Tag(nextValue, next.color)
            }

            override fun hasNext(): Boolean = next != null

            override fun next(): Tag = next.also { next = fetchNext() } ?: throw NoSuchElementException()

            override fun remove() {
                throw UnsupportedOperationException("Deletion within iterator not supported")
            }

        }
    }


    // --- Mutable collection methods ---

    /**
     * Add a tag string to the tree.
     *
     * @param element The tag to add. For example, if this node's value is
     *               "foo", and you want to add "foo.bar.baz", tag would be
     *               "foo.bar.baz".
     */
    override fun add(element: Tag): Boolean {
        val subTag = getSubtag(element.text).second ?: return false
        return add(subTag, element.color)
    }

    /**
     * Add a tag string to the tree.
     *
     * @param subTag A subtag of this tag. For example, if this node's value is
     *               "foo", and you want to add "foo.bar.baz", subTag would be
     *               "bar.baz".
     */
    private fun add(subTag: String, color: Color): Boolean {
        if (subTag.isEmpty()) return false // cannot add empty tag
        val split = getSubtag(subTag)

        // if next tag will be the final tag in the recursive call,
        // if it's new, make it the given color, else, if it's new, make it
        // a random color
        val nextNewTagColor = if (split.second != null && split.second == getSubtag(split.second!!).first)
            color
        else
            Color.randomTagColor()


        // get existing child or create new one for next part of tag
        val child: TagTree = children.firstOrNull { it.value == split.first }
            ?: TagTree(split.first, nextNewTagColor).also { children.add(it) }

        child.add(split.second ?: "", color)
        return true
    }

    override fun addAll(elements: Collection<Tag>): Boolean = elements.all { add(it) }

    override fun clear() {
        this.children.clear()
    }

    /**
     * Try to remove a given element from this tree. Will only remove elements
     * that have no children.
     *
     * @param element - if this node's value is "foo", and you want to remove
     *                  "foo.bar" element would be the full string "foo.bar"
     */
    override fun remove(element: Tag): Boolean = remove(element.text)

    /**
     * Try to remove a given element from this tree. Will only remove elements
     * that have no children.
     *
     * @param element - if this node's value is "foo", and you want to remove
     *                  "foo.bar", element would be the full string "foo.bar"
     */
    private fun remove(element: String): Boolean {
        if (element.isEmpty()) return false // cannot have empty tag
        val split = getSubtag(element)

        if (split.first != this.value || split.second == null) return false

        // get next matching child, or return false if not found
        val childValue = getSubtag(split.second!!).first
        val child: TagTree = children.firstOrNull { it.value == childValue }
            ?: return false

        if (child.value == split.second) {
            // child is a match
            return if (child.isLeaf) children.remove(child) else false
        }

        return child.remove(split.second ?: "")
    }

    override fun removeAll(elements: Collection<Tag>): Boolean = elements.all { remove(it) }

    override fun retainAll(elements: Collection<Tag>): Boolean {

        // cannot retail all if they aren't all here
        if (!containsAll(elements)) return false

        clear()
        return addAll(elements)
    }
}