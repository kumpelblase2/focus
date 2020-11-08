package de.eternalwings.focus.storage.data

import de.eternalwings.focus.storage.FilenameConstants
import de.eternalwings.focus.storage.FilenameConstants.CHANGESET_FILE_REGEX
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime

object ChangesetFilenameParser {
    fun getInformationOf(path: Path): ChangesetDescription? {
        if (!Files.exists(path)) return null

        val filenameMatch = CHANGESET_FILE_REGEX.matchEntire(path.fileName.toString()) ?: return null
        val timestampValue = filenameMatch.groupValues[1]
        val timestamp = if (timestampValue != FilenameConstants.CHANGESET_INIT_TIMESTAMP) {
            LocalDateTime.parse(timestampValue, FilenameConstants.CHANGESET_TIME_FORMAT)
        } else {
            LocalDateTime.MIN
        }
        val previousId = filenameMatch.groupValues[2]
        val id = filenameMatch.groupValues[3]
        return SimpleChangesetDescription(id, previousId, timestamp)
    }

    fun toFilename(changesetDescription: ChangesetDescription): String {
        val timestamp = if(changesetDescription.timestamp == LocalDateTime.MIN) {
            FilenameConstants.CHANGESET_INIT_TIMESTAMP
        } else {
            changesetDescription.timestamp.format(FilenameConstants.CHANGESET_TIME_FORMAT)
        }

        return "${timestamp}=${changesetDescription.previousId}+${changesetDescription.id}.zip"
    }
}
