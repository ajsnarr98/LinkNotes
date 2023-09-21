package com.github.ajsnarr98.linknotes.network.domain

import com.github.ajsnarr98.linknotes.network.TagsRepository
import com.github.ajsnarr98.linknotes.network.util.Color

// TODO test this

/**
 * A set specifically for holding tags where they can be represented in a
 * tree format that helps with auto-completion.
 *
 * This class is only used for the uppermost root node of the tree.
 */
class TagSet private constructor(
    override val children: Set<NonRootNode>,
) : AbstractTagTreeNode<TagSet>() {

    constructor(collection: Collection<Tag>) : this(
        children = collection
            .asSequence()
            .filter { tag -> TagsRepository.isValidTag(tag) }
            .map { tag ->
                val (start, rest) = getSubtag(tag.text)
                start to (tag to rest)
            }
            .groupBy(
                keySelector = { (k, _) -> k },
                valueTransform = { (_, v) -> v },
            )
            .map { (key, values) ->
                val color = values
                    .firstOrNull { (_, rest) -> rest == null }?.first?.color ?: Color.randomTagColor()
                NonRootNode(
                    value = key,
                    color = color,
                    collection = values.mapNotNull { (tag, rest) -> if (rest == null) null else (tag to rest) }
                )
            }.toSet()
    )

    override val value: String = ""

    override fun isEmpty(): Boolean = children.isNotEmpty()

    override fun contains(element: String): Boolean {
        if (!TagsRepository.isValidTag(element)) return false

        val (nextVal, _) = getSubtag(element)
        val next = children.firstOrNull { child -> child.value == nextVal }
        return next != null && next.contains(element)
    }

    override suspend fun SequenceScope<Tag>.dfsValues(
        thisNode: AbstractTagTreeNode<*>,
        previous: String
    ) {
        for (child in children) {
            dfsValues(child, "")
        }
    }

    override fun plus(element: Tag): TagSet = TagSet(this.toList() + element)
    override fun minus(element: Tag): TagSet = TagSet(HashSet(this) - element)
}

/**
 * Represents a node in a tree of strings, representing tags.
 *
 * For example "classes.jmu" would be the "jmu" tag as a leaf under "classes".
 *
 * The root of all tags is ultimately the empty tag "", but this tag is invalid.
 */
sealed class AbstractTagTreeNode<T : AbstractTagTreeNode<T>>: Set<Tag>, AppDataObject {

    abstract val value: String
    abstract val children: Set<AbstractTagTreeNode<*>>

    companion object {

        /**
         * Split "classes.jmu.gen-ed" into "classes" and "jmu.gen-ed".
         *
         * If no separators left to split off, return null as second value.
         */
        fun getSubtag(tag: String): Pair<String, String?> {
            if (tag.isEmpty()) throw IllegalArgumentException("Invalid tag. Tag cannot be empty")
            val separatorInd = tag.indexOf(TagsRepository.SEPARATOR)
            return when {
                // if separator not found
                separatorInd < 0 -> Pair(tag, null)
                separatorInd == 0 -> throw IllegalArgumentException("Invalid tag. Tag cannot start with ${TagsRepository.SEPARATOR}")
                // make sure there is still space after separator for subtag
                separatorInd < tag.length - 1 -> Pair(tag.substring(0, separatorInd), tag.substring(separatorInd + 1))
                // there were no other characters after separator
                else -> throw IllegalArgumentException("Invalid tag. Tag cannot end with ${TagsRepository.SEPARATOR}")
            }
        }
    }

    class NonRootNode(
        override val value: String,
        val color: Color,
        override val children: Set<NonRootNode>,
    ) : AbstractTagTreeNode<NonRootNode>() {

        /**
         * Assumes all tags in given collection start with the same string
         * before the first (if any) separator.
         *
         * Given collection cannot be empty.
         */
        constructor(
            collection: Collection<Tag>,
        ) : this(
            value = getSubtag(
                collection.firstOrNull()?.text
                    ?: throw IllegalArgumentException("Given collection cannot be empty")
            ).first,
            color = collection.firstOrNull { tag -> getSubtag(tag.text).second == null }?.color
                ?: Color.randomTagColor(),
            collection = collection.mapNotNull { tag ->
                val (_, rest) = getSubtag(tag.text)
                if (rest != null) (tag to rest) else null
            }
        )

        /**
         * @param collection full collection underneath this value in tree.
         */
        constructor(
            value: String,
            color: Color,
            collection: Collection<Pair<Tag, String>>,
        ) : this(
            value = value,
            color = color,
            children = collection
                .asSequence()
                .filter { (tag, _) -> TagsRepository.isValidTag(tag) }
                .map { (tag, rest) ->
                    val (start, newRest) = getSubtag(rest)
                    start to (tag to newRest)
                }
                .groupBy(
                    keySelector = { (k, _) -> k },
                    valueTransform = { (_, v) -> v },
                )
                .map { (key, values) ->
                    val nextColor = values
                        .firstOrNull { (_, rest) -> rest == null }?.first?.color ?: Color.randomTagColor()
                    NonRootNode(
                        value = key,
                        color = nextColor,
                        collection = values.mapNotNull { (tag, rest) -> if (rest == null) null else (tag to rest) }
                    )
                }.toSet()
        )

        override fun isEmpty(): Boolean {
            // at the least, this node exists in the tree
            return false
        }

        override fun contains(element: String): Boolean {
            val (myVal, nextSection) = getSubtag(element)
            return myVal == this.value || nextSection != null && run {
                val (nextVal, _) = getSubtag(nextSection)
                val next = children.firstOrNull { child -> child.value == nextVal }
                return@run next != null && next.contains(nextSection)
            }
        }

        override suspend fun SequenceScope<Tag>.dfsValues(
            thisNode: AbstractTagTreeNode<*>,
            previous: String
        ) {
            yield(Tag("${previous}${thisNode.value}"))
            val next = "${previous}${thisNode.value}${TagsRepository.SEPARATOR}"
            for (child in children) {
                dfsValues(child, next)
            }
        }

        override fun plus(element: Tag): NonRootNode = NonRootNode(this.toList() + element)
        override fun minus(element: Tag): NonRootNode = NonRootNode(HashSet(this) - element)
    }

    private val isLeaf: Boolean
        get() = children.isEmpty()

    override fun equals(other: Any?): Boolean {
        // ignore children and color
        return this === other
                || other is AbstractTagTreeNode<*> && this.value == other.value
    }

    override fun hashCode(): Int {
        // ignore children and color
        return value.hashCode()
    }

    // --- Collection methods ---

    override val size: Int
        get() = if (isLeaf) { 1 } else { children.sumOf { tree -> tree.size } + 1 }

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
    abstract fun contains(element: String): Boolean

    override fun containsAll(elements: Collection<Tag>): Boolean = elements.all { contains(it) }

    override fun iterator(): Iterator<Tag> = sequence<Tag> {
        TODO("Not yet implemented")
    }.iterator()

    /**
     * Used for implementing iterator. Yields tags to sequence.
     *
     * @param previous part of this tag's full string including a separator at the end
     */
    protected abstract suspend fun SequenceScope<Tag>.dfsValues(thisNode: AbstractTagTreeNode<*>, previous: String)

    abstract operator fun plus(element: Tag): T

    abstract operator fun minus(element: Tag): T
}