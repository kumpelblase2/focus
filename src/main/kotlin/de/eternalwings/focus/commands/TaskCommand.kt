package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands

class TaskCommand :
    NoOpCliktCommand(name = "tasks", help = "Subcommands for interacting with tasks inside the storage.") {

    init {
        subcommands(CreateTaskCommand(), DoTaskCommand())
    }
}
