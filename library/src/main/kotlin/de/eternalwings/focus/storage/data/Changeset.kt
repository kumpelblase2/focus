package de.eternalwings.focus.storage.data

import de.eternalwings.focus.storage.FilenameConstants
import java.time.LocalDateTime

data class Changeset(
    val timestamp: LocalDateTime,
    val id: String,
    val previousId: String,
    val container: OmniContainer
) {
    fun createFilename(): String {
        return if (timestamp == LocalDateTime.MIN) {
            FilenameConstants.CHANGESET_INIT_TIMESTAMP
        } else {
            FilenameConstants.CHANGESET_TIME_FORMAT.format(timestamp)
        } + "=" + previousId + "+" + id + ".zip"
    }
}

