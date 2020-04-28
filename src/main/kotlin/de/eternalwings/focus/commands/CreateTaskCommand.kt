package de.eternalwings.focus.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import de.eternalwings.focus.view.OmniTask

class CreateTaskCommand :
    UnlockedStorageBasedCommand(name = "create", help = "Adds a new task to a project or the inbox.") {

    val text by argument("task")

    override fun run() {
        val storage = getUnlockedStorage()
//        val task =
    }
}
