package de.eternalwings.focus.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.failWith
import de.eternalwings.focus.storage.PhysicalStorage
import java.nio.charset.StandardCharsets

class ShowFileCommand : UnlockedStorageBasedCommand(
    name = "show-changeset-file",
    help = "Shows the contents of a changeset in the given storage."
) {

    val id by argument(name = "id", help = "The id of the changeset to display")

    override fun run() {
        val storage = getUnlockedStorage()
        if (storage !is PhysicalStorage) {
            failWith("", ErrorCodes.INTERNAL_ERROR)
        }

        val foundFile = storage.changeSetFiles.find { it.id == id }
            ?: failWith("No such file found.", ErrorCodes.GENERIC_ARGUMENT_ERROR)

        val content = String(storage.getContentOfFile(foundFile), StandardCharsets.UTF_8)
        print(content)
    }
}
