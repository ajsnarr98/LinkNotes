package com.ajsnarr.linknotes

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
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
    var hasAddButton: Boolean = false
        private set

    private var onAddButtonClick: () -> Unit = {}
    private val tags: MutableSet<Tag> = mutableSetOf()

    constructor(context: Context) : super(context) { init(null) }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) { init(attrs) }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) { init(attrs) }

    fun init(attrs: AttributeSet?) {
        // get styled attributes
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.TagChipGroup,
            0,
            0
        ).apply {
                try {
                    hasAddButton = getBoolean(R.styleable.TagChipGroup_hasAddButton, false)
                } finally {
                    recycle() // must recycle typed array
                }
        }
    }

    /**
     * Add a tag to this group.
     */
    fun addTag(tag: Tag) {
        this.tags.add(tag)
        refresh()
    }

    /**
     * Add a collection of tags to this group.
     */
    fun addTags(tags: Collection<Tag>) {
        this.tags.addAll(tags)
        refresh()
    }

    /**
     * Set the listener for when the add button is pressed.
     */
    fun setOnAddButtonClickListener(onAddButtonClick: () -> Unit) {
        this.onAddButtonClick = onAddButtonClick
    }

    /**
     * Create a chip for the given tag.
     */
    private fun asChip(tag: Tag): Chip = Chip(context).apply {
        text = tag.text
        chipBackgroundColor = ColorStateList.valueOf(tag.color.asInt())
    }

    /**
     * Create a chip for the add button.
     */
    private fun createAddNewTagsChip(): Chip = Chip(context).apply {
        text = context.getString(R.string.editnote_add_tag_chip_text)
        chipBackgroundColor = context.getColorStateList(R.color.tag_button_blue)
        chipIcon = ResourcesCompat.getDrawable(context.resources, R.drawable.ic_add_24, context.theme)
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
        for (tag in tags) {
            this.addView(asChip(tag))
        }

        // add 'add tags' button
        if (hasAddButton) {
            this.addView(createAddNewTagsChip())
        }
    }
}