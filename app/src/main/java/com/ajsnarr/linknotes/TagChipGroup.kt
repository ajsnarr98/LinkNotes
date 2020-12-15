package com.ajsnarr.linknotes

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.annotation.StyleRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.ajsnarr.linknotes.data.Tag
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup


/**
 * A ChipGroup that holds chips pertaining to tags.
 *
 * @property hasAddButton - whether or not to include a chip at the end of
 *                          the list for adding tags
 * @property onAddButtonClick - listener to be called when a the add button is
 *                              clicked. Only used when hasAddButton is true
 */
class TagChipGroup : ChipGroup {

    /**
     * Whether or not this chip group has an add button.
     */
    private var hasAddButton: Boolean = false

    /**
     * Whether or not to show the close icon on each chip.
     */
    private var showCloseIcons: Boolean = false

    /**
     * Optional chip style.
     */
    @StyleRes
    private var chipStyle: Int? = null

    private var onAddButtonClick: () -> Unit = {}
    private var onTagClick: (tag: Tag) -> Unit = {}
    private var areTagsClickable: Boolean = false

    private val invisibleRippleColor: ColorStateList by lazy {
        ColorStateList.valueOf(ContextCompat.getColor(context, R.color.transparent))
    }

    private val _tags: MutableSet<Tag> = mutableSetOf()

    val tags: Set<Tag> get() = _tags

    constructor(context: Context) : super(context) { init(null) }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs) }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) { init(attrs) }

    fun init(attrs: AttributeSet?) {
        // get styled attributes
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TagChipGroup,
            0,
            0
        ).apply {
                try {
                    hasAddButton = getBoolean(R.styleable.TagChipGroup_hasAddButton, hasAddButton)
                    showCloseIcons = getBoolean(
                        R.styleable.TagChipGroup_showCloseIcons,
                        showCloseIcons
                    )
                    chipStyle = getResourceId(R.styleable.TagChipGroup_chipStyle, 0).let { id -> if (id != 0) id else null }
                } finally {
                    recycle() // must recycle typed array
                }
        }
    }

    /**
     * Add a tag to this group.
     */
    fun addTag(tag: Tag) {
        this._tags.add(tag)
        refresh()
    }

    /**
     * Remove a tag from this group.
     */
    fun removeTag(tag: Tag) {
        this._tags.remove(tag)
        refresh()
    }

    /**
     * Add a collection of tags to this group.
     */
    fun addTags(tags: Collection<Tag>) {
        this._tags.addAll(tags)
        refresh()
    }

    /**
     * Sets the collection of tags to this group.
     */
    fun setTags(tags: Collection<Tag>) {
        this._tags.clear()
        addTags(tags)
    }

    /**
     * Set the listener for when the add button is pressed.
     */
    fun setOnAddButtonClickListener(onAddButtonClick: () -> Unit) {
        this.onAddButtonClick = onAddButtonClick
    }

    /**
     * Set the listener for when the chip for a tag is pressed.
     */
    fun setOnTagClickListener(onTagClick: (tag: Tag) -> Unit) {
        this.onTagClick = onTagClick
        this.areTagsClickable = true
    }

    /**
     * Get the base chip with the correct style.
     */
    private fun baseChip(): Chip = chipStyle?.let { style ->
        Chip(context, null, style)
    } ?: Chip(context)


    /**
     * Create a chip for the given tag.
     */
    private fun asChip(tag: Tag): Chip = baseChip().apply {
        text = tag.text
        chipBackgroundColor = ColorStateList.valueOf(tag.color.asInt())
        isCloseIconVisible = showCloseIcons
        if (areTagsClickable) {
            setOnClickListener { onTagClick(tag) }
        } else {
            rippleColor = invisibleRippleColor
        }
    }

    /**
     * Create a chip for the add button.
     */
    private fun createAddNewTagsChip(): Chip = baseChip().apply {
        text = context.getString(R.string.editnote_add_tag_chip_text)
        chipIcon = ResourcesCompat.getDrawable(
            context.resources,
            R.drawable.ic_add_24,
            context.theme
        )
        textStartPadding = 0f
        textEndPadding = 0f
        chipStartPadding = context.resources.getDimension(R.dimen.quarter_base_padding)
        chipEndPadding = context.resources.getDimension(R.dimen.quarter_base_padding)
        setOnClickListener { onAddButtonClick() }
    }

    /**
     * Refresh the views in the chip group.
     *
     * TODO - make this recycle unchanged views
     */
    private fun refresh() {
        // clear old tags
        this.removeAllViews()

        // add each tag
        for (tag in _tags) {
            this.addView(asChip(tag))
        }

        // add 'add tags' button
        if (hasAddButton) {
            this.addView(createAddNewTagsChip())
        }
    }
}