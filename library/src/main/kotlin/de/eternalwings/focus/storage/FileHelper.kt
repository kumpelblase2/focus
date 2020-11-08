package de.eternalwings.focus.storage

import de.eternalwings.focus.storage.FilenameConstants.CAPABILITY_FILE_NAME
import de.eternalwings.focus.storage.FilenameConstants.CHANGESET_FILE_REGEX
import de.eternalwings.focus.storage.FilenameConstants.CLIENT_FILE_NAME
import java.nio.file.Path
import java.time.OffsetDateTime

object FileHelper {
    fun isClientFile(path: Path): Boolean {
        return path.fileName.toString().endsWith(CLIENT_FILE_NAME)
    }

    fun formatClientFileName(clientId: String, lastSync: OffsetDateTime): String {
        return FilenameConstants.CLIENT_FILE_DATE_FORMAT.format(lastSync) + "=" + clientId + CLIENT_FILE_NAME
    }

    fun isCapabilityFile(path: Path) : Boolean {
        return path.fileName.toString().endsWith(CAPABILITY_FILE_NAME)
    }

    fun formatCapabilityFileName(capability: String): String {
        return capability + CAPABILITY_FILE_NAME
    }

    fun isChangesetFileName(path: Path) : Boolean {
        return CHANGESET_FILE_REGEX.matchEntire(path.fileName.toString()) != null
    }
}
