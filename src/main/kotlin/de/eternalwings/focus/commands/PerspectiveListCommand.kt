package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.google.gson.GsonBuilder
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.config.config

class PerspectiveListCommand :
    CliktCommand(name = "list", help = "Lists all currently registered custom perspectives.") {

    private val gson = GsonBuilder().setPrettyPrinting().create()
    val json by option("--json", help = "Print out in json").flag()

    override fun run() {
        val perspectives = config[Configuration.perspectives]
        if (json) {
            println(gson.toJson(perspectives))
        } else {
            if (perspectives.isNotEmpty()) {
                perspectives.forEach { perspective ->
                    println("${perspective.name} - ${perspective.query}")
                }
            } else {
                println("No perspectives registered")
            }
        }
    }

}
