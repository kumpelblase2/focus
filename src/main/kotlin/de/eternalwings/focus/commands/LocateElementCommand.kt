package de.eternalwings.focus.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.failWith

class LocateElementCommand :
    UnlockedStorageBasedCommand(name = "locate", help = "Locates the changesets that an element is contained in.") {

    val id by argument("element-id", help = "The ID of the element to locate.")

    override fun run() {
        val storage = getUnlockedStorage()
        val changesetIds =
            storage.changeSets.asSequence().filter { changeset -> changeset.container.content.any { it.id == id } }
                .map { it.id }.toList()

        if(changesetIds.isEmpty()) {
            failWith("The element was not found in the store.", ErrorCodes.ELEMENT_NOT_FOUND)
        }
    }
}
