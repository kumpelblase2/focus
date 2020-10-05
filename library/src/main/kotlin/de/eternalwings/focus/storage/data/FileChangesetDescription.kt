package de.eternalwings.focus.storage.data

import java.nio.file.Path
import java.time.LocalDateTime

data class FileChangesetDescription(
    val path: Path,
    override val timestamp: LocalDateTime,
    override val id: String,
    override val previousId: String
) : ChangesetDescription {
    companion object {
        fun fromFile(path: Path): FileChangesetDescription? {
            val description = ChangesetFilenameParser.getInformationOf(path) ?: return null
            return FileChangesetDescription(path, description.timestamp, description.id, description.previousId)
        }
    }
}
