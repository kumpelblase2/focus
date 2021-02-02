package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import de.eternalwings.focus.commands.task.CancelTaskCommand
import de.eternalwings.focus.commands.task.CreateTaskCommand
import de.eternalwings.focus.commands.task.DeferTaskCommand
import de.eternalwings.focus.commands.task.DoTaskCommand

class TaskCommand :
    NoOpCliktCommand(name = "tasks", help = "Subcommands for interacting with tasks inside the storage.") {

    init {
        subcommands(CreateTaskCommand(), DoTaskCommand(), DeferTaskCommand(), CancelTaskCommand())
    }
}
