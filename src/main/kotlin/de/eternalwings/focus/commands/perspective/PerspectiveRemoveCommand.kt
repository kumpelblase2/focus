package de.eternalwings.focus.commands.perspective

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import de.eternalwings.focus.config.Configuration

class PerspectiveRemoveCommand : CliktCommand(name = "remove", help = "Removes the perspective with the given name.") {

    val name by argument(help = "The name of the perspective to remove.")

    override fun run() {
        val perspectives = Configuration.instance.perspectives
        val filtered = perspectives.filter { it.key != name }
        Configuration.instance.perspectives = filtered
        Configuration.save()
    }

}
