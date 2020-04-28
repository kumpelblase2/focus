package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands

class PerspectiveCommand :
    NoOpCliktCommand(name = "perspective", help = "Sub-command for perspective related actions.") {
    init {
        subcommands(PerspectiveCreateCommand(), PerspectiveRemoveCommand(), PerspectiveListCommand())
    }
}
