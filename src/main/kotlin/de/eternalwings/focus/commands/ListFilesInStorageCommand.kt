package de.eternalwings.focus.commands

import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.failWith
import de.eternalwings.focus.storage.PhysicalStorage
import java.time.LocalDateTime

class ListFilesInStorageCommand :
    StorageBasedCommand(name = "list-changesets", help = "Lists all the available changesets in the store.") {

    override fun run() {
        val storage = loadStorage()
        if (storage !is PhysicalStorage) {
            failWith("Internal problem - storage is not physical?!", ErrorCodes.INTERNAL_ERROR)
        }

        storage.changeSetFiles.forEach { file ->
            val timestampValue = if (file.timestamp == LocalDateTime.MIN) {
                "<Initial>\t\t"
            } else {
                file.timestamp.toString()
            }
            println("Timestamp: " + timestampValue + "\t ID: " + file.id)
        }
    }
}
