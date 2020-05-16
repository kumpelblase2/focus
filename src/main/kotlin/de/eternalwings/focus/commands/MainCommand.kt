package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands

class MainCommand() : NoOpCliktCommand(
    name = "focus",
    help = """
    This program can read and filter elements from an omnifocus database. If it's encrypted, 
    providing a password also allows the program to decrypt the database before and thus continue
    like normal.
    
    The QUERY allows filtering the found tasks to show only the ones interested in. You can 
    find more information about the query language at BLA.
""".trimIndent()
) {
    init {
        subcommands(
            QueryCommand(),
            DeviceCommand(),
            CreateConfigCommand(),
            PerspectiveCommand(),
            StorageCommand(),
            TaskCommand(),
            VersionCommand()
        )
    }
}
