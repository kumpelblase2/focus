package de.eternalwings.focus.storage.data

data class Changeset(
    val timestamp: Long,
    val id: String,
    val previousId: String,
    private val loader: () -> OmniContainer
) {
    val contentContainer: OmniContainer by lazy(loader)
}

