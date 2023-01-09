package com.github.ajsnarr98.linknotes.network.domain

import com.github.ajsnarr98.linknotes.data.UUID
import com.github.ajsnarr98.linknotes.network.GlobalScopedDependencies
import java.io.Serializable
import java.util.Date

sealed class Entry<T : Entry<T>> : AppDataObject, Serializable {

    companion object {
        const val DEFAULT_INVALID_UUID: UUID = "-1"

        fun <T : Entry<T>> forType(
            id: UUID,
            type: EntryType<T>,
            isDeletable: Boolean = true,
            priority: EntryPriority = EntryPriority.LOWEST,
        ): Entry<T> {
            return when (type) {
                is EntryType.Default -> Default(
                    id = id,
                    content = EntryContent.Text.EMPTY,
                    priority = priority,
                    isDeletable = isDeletable,
                    timeCreated = GlobalScopedDependencies.timeProvider.now(),
                    lastTimeEdited = GlobalScopedDependencies.timeProvider.now(),
                    subEntries = EntryList.getEmpty(),
                )
                is EntryType.Images -> Images(
                    id = id,
                    content = EntryContent.Images.EMPTY,
                    priority = priority,
                    isDeletable = isDeletable,
                    timeCreated = GlobalScopedDependencies.timeProvider.now(),
                    lastTimeEdited = GlobalScopedDependencies.timeProvider.now(),
                    subEntries = EntryList.getEmpty(),
                )
                is EntryType.Custom -> Custom(
                    id = id,
                    content  = EntryContent.Text.EMPTY,
                    priority = priority,
                    isDeletable = isDeletable,
                    timeCreated = GlobalScopedDependencies.timeProvider.now(),
                    lastTimeEdited = GlobalScopedDependencies.timeProvider.now(),
                    subEntries = EntryList.getEmpty(),
                    type = type,
                )
            } as Entry<T>
        }
    }

    abstract val id: UUID
    abstract val content: EntryContent
    abstract val priority: EntryPriority
    abstract val timeCreated: Date
    abstract val lastTimeEdited: Date
    abstract val isDeletable: Boolean
    abstract val subEntries: EntryList

    abstract val type: EntryType<T>

    /**
     * Fills up any empty fields with default values. Used after saving an
     * incomplete new note.
     */
    abstract fun withFilledDefaults(): T

    abstract fun withId(id: UUID): T

    // TODO add sub-entry logic

    data class Default(
        override val id: UUID,
        override val content: EntryContent,
        override val priority: EntryPriority,
        override val timeCreated: Date,
        override val lastTimeEdited: Date,
        override val isDeletable: Boolean,
        override val subEntries: EntryList,
    ) : Entry<Default>() {
        override val type: EntryType<Default>
            get() = EntryType.Default

        /**
         * Similar to the copy() method, but also updates the last edited time automatically.
         */
        fun edited(
            id: UUID = this.id,
            content: EntryContent = this.content,
            priority: EntryPriority = this.priority,
            timeCreated: Date = this.timeCreated,
            isDeletable: Boolean = this.isDeletable,
            subEntries: EntryList = this.subEntries,
        ): Default = Default(
            id = id,
            content = content,
            priority = priority,
            timeCreated = timeCreated,
            lastTimeEdited = GlobalScopedDependencies.timeProvider.now(),
            isDeletable = isDeletable,
            subEntries = subEntries,
        )

        override fun withFilledDefaults(): Default = edited(
            subEntries = this.subEntries.withFilledDefaults()
        )

        override fun withId(id: UUID): Default = edited(
            id = id,
        )
    }

    data class Images(
        override val id: UUID,
        override val content: EntryContent.Images,
        override val priority: EntryPriority,
        override val timeCreated: Date,
        override val lastTimeEdited: Date,
        override val isDeletable: Boolean,
        override val subEntries: EntryList,
    ) : Entry<Images>() {
        override val type: EntryType<Images>
            get() = EntryType.Images

        /**
         * Returns a copy of the entry with an image added at the end.
         */
        fun withImageAppended(imageUrl: String): Images = edited(
            content = content.withImageAppended(imageUrl)
        )


        /**
         * Returns a copy of the entry with the first image with matching
         * url removed.
         */
        fun withImageRemoved(imageUrl: String): Images = edited(
            content = content.withImageRemoved(imageUrl)
        )

        /**
         * Similar to the copy() method, but also updates the last edited time automatically.
         */
        fun edited(
            id: UUID = this.id,
            content: EntryContent.Images = this.content,
            priority: EntryPriority = this.priority,
            timeCreated: Date = this.timeCreated,
            isDeletable: Boolean = this.isDeletable,
            subEntries: EntryList = this.subEntries,
        ): Images = Images(
            id = id,
            content = content,
            priority = priority,
            timeCreated = timeCreated,
            lastTimeEdited = GlobalScopedDependencies.timeProvider.now(),
            isDeletable = isDeletable,
            subEntries = subEntries,
        )

        override fun withFilledDefaults(): Images = edited(
            subEntries = this.subEntries.withFilledDefaults()
        )

        override fun withId(id: UUID): Images = edited(
            id = id,
        )
    }

    data class Custom(
        override val id: UUID,
        override val content: EntryContent,
        override val priority: EntryPriority,
        override val timeCreated: Date,
        override val lastTimeEdited: Date,
        override val isDeletable: Boolean,
        override val subEntries: EntryList,
        override val type: EntryType.Custom,
    ) : Entry<Custom>() {
        /**
         * Similar to the copy() method, but also updates the last edited time automatically.
         */
        fun edited(
            id: UUID = this.id,
            content: EntryContent = this.content,
            priority: EntryPriority = this.priority,
            timeCreated: Date = this.timeCreated,
            isDeletable: Boolean = this.isDeletable,
            subEntries: EntryList = this.subEntries,
        ): Custom = Custom(
            id = id,
            content = content,
            priority = priority,
            timeCreated = timeCreated,
            lastTimeEdited = GlobalScopedDependencies.timeProvider.now(),
            isDeletable = isDeletable,
            subEntries = subEntries,
            type = type,
        )

        override fun withFilledDefaults(): Custom = edited(
            subEntries = this.subEntries.withFilledDefaults()
        )

        override fun withId(id: UUID): Custom = edited(
            id = id,
        )
    }
}
