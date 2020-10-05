package de.eternalwings.focus.storage.data

import java.time.LocalDateTime

data class SimpleChangesetDescription(
    override val id: String,
    override val previousId: String,
    override val timestamp: LocalDateTime
) : ChangesetDescription

interface ChangesetDescription {
    val id: String
    val previousId: String
    val timestamp: LocalDateTime
}
