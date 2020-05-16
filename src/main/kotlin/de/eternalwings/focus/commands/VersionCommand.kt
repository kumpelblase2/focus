package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.CliktCommand

class VersionCommand : CliktCommand(name = "version", help = "Prints version information") {

    override fun run() {
        println("focus cli v$version")
    }

    companion object {
        val version: String
            get() = VersionCommand::class.java.`package`.implementationVersion
    }
}
