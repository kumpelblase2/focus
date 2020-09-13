package de.eternalwings.focus.storage.data

/**
 * An OmniContainer contains all the information that is present inside a [Changeset].
 * You can think of this as the root XML node inside the changeset file. It contains
 * the information about which device created this changeset as well as the actual
 * contents, so the changes, of the changeset.
 */
data class OmniContainer(
    val creator: ContentCreator,
    val content: List<ChangesetElement>
)
