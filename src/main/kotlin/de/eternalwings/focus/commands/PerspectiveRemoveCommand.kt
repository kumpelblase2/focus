package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.config.config
import de.eternalwings.focus.config.save

class PerspectiveRemoveCommand : CliktCommand(name = "remove", help = "Removes the perspective with the given name.") {

    val name by argument(help = "The name of the perspective to remove.")

    override fun run() {
        val perspectives = config[Configuration.perspectives]
        val filtered = perspectives.filter { it.name != name }
        config[Configuration.perspectives] = filtered
        config.save()
    }

}
