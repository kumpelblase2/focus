package de.eternalwings.focus.commands.storage

import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import de.eternalwings.focus.commands.StorageBasedCommand
import de.eternalwings.focus.presentation.GsonProvider
import java.time.LocalDateTime

class ListFilesInStorageCommand :
    StorageBasedCommand(name = "list-changesets", help = "Lists all the available changesets in the store.") {

    val idOnly by option("--id-only", "-i", help = "Only display the ID of each changeset").flag()
    val json by option("--json", "-j", help = "Output as JSON").flag()

    private data class ChangesetData(val timestamp: String, val id: String)

    override fun run() {
        val storage = loadStorage()
        val data = storage.changesetInformation.map { file ->
            val timestamp = if (file.timestamp == LocalDateTime.MIN) {
                "<Initial>\t"
            } else {
                file.timestamp.toString()
            }
            ChangesetData(timestamp, file.id)
        }

        if (json) {
            this.printJsonChangesets(data)
        } else {
            data.forEach(this::printChangeset)
        }
    }

    private fun printJsonChangesets(data: List<ChangesetData>) {
        val message = if (idOnly) {
            GsonProvider.INSTANCE.toJson(data.map { it.id })
        } else {
            GsonProvider.INSTANCE.toJson(data)
        }
        println(message)
    }

    private fun printChangeset(file: ChangesetData) {
        if (idOnly) {
            println(file.id)
        } else {
            println("Timestamp: " + file.timestamp + "\t ID: " + file.id)
        }
    }
}
