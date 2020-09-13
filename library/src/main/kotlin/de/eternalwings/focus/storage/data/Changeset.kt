package de.eternalwings.focus.storage.data

import de.eternalwings.focus.storage.FilenameConstants
import java.time.LocalDateTime

/**
 * A changeset is a self-contained and isolated collection of changes. It contains at most one
 * change per element, i.e. an element with a specific ID will only appear once in the whole changeset.
 * Additionally, changesets are ordered by which changeset they're based upon, except for the changeset
 * with the timestamp of "00000000000000" which denotes the changeset to be the "root" or "genesis" changeset.
 *
 */
data class Changeset(
    val timestamp: LocalDateTime,
    val id: String,
    val previousId: String,
    val container: OmniContainer
) {
    val isRootChangeset = timestamp == LocalDateTime.MIN

    fun createFilename(): String {
        return if (isRootChangeset) {
            FilenameConstants.CHANGESET_INIT_TIMESTAMP
        } else {
            FilenameConstants.CHANGESET_TIME_FORMAT.format(timestamp)
        } + "=" + previousId + "+" + id + ".zip"
    }
}

