package de.eternalwings.focus.storage.data

/**
 * An OmniContainer contains all the information that is present inside a [Changeset].
 * You can think of this as the root XML node inside the changeset file. It contains
 * the information about which device created this changeset as well as the actual
 * contents, so the changes, of the changeset.
 */
data class OmniContainer(
    /**
     * The creator who made the changeset these changes belong to
     */
    val creator: ContentCreator,
    /**
     * The changes of the changeset
     */
    val content: List<ChangesetElement>
)
