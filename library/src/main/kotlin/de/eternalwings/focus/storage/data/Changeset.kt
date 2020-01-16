package de.eternalwings.focus.storage.data

data class Changeset(
    val timestamp: Long,
    val id: String,
    val previousId: String,
    val container: OmniContainer
)

