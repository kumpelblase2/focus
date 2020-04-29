package de.eternalwings.focus.commands

import java.time.LocalDateTime

class ListFilesInStorageCommand :
    StorageBasedCommand(name = "list-changesets", help = "Lists all the available changesets in the store.") {

    override fun run() {
        val storage = loadStorage()
        storage.changeSetFiles.forEach { file ->
            val timestampValue = if (file.timestamp == LocalDateTime.MIN) {
                "<Initial>\t"
            } else {
                file.timestamp.toString()
            }
            println("Timestamp: " + timestampValue + "\t ID: " + file.id)
        }
    }
}
