package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands
import de.eternalwings.focus.commands.perspective.PerspectiveCreateCommand
import de.eternalwings.focus.commands.perspective.PerspectiveListCommand
import de.eternalwings.focus.commands.perspective.PerspectiveRemoveCommand

class PerspectiveCommand :
    NoOpCliktCommand(name = "perspective", help = "Sub-command for perspective related actions.") {
    init {
        subcommands(PerspectiveCreateCommand(), PerspectiveRemoveCommand(), PerspectiveListCommand())
    }
}
