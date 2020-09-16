package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.presentation.GsonProvider

class PerspectiveListCommand :
    CliktCommand(name = "list", help = "Lists all currently registered custom perspectives.") {

    private val gson = GsonProvider.INSTANCE
    private val json by option("-j", "--json", help = "Print out in json").flag()

    override fun run() {
        val perspectives = Configuration.instance.perspectives
        if (json) {
            println(gson.toJson(perspectives))
        } else {
            if (perspectives.isNotEmpty()) {
                perspectives.forEach { perspective ->
                    println("${perspective.key} - ${perspective.value}")
                }
            } else {
                println("No perspectives registered")
            }
        }
    }

}
