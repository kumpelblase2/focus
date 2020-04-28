package de.eternalwings.focus.storage.data

import de.eternalwings.focus.storage.FilenameConstants
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

data class ChangesetFile(val path: Path, val timestamp: LocalDateTime, val id: String, val previousId: String) {
    companion object {

        private val CHANGESET_FILE_REGEX = "^(\\d{14})=(.{11})\\+(.{11})\\.zip$".toRegex()

        fun fromFile(path: Path): ChangesetFile? {
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
            return ChangesetFile(path, timestamp, id, previousId)
        }
    }
}
