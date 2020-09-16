package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands

class MainCommand() : NoOpCliktCommand(
    name = "focus",
    help = """
    This program provides multiple ways to interact with an existing omnifocus 
    database, including decrypting, displaying tasks, listing devices and updating 
    tasks. It is meant as a minimal replacement to the official applications or
    for systems which are unsupported.
    
    The program is structured into multiple commands which may contain additional
    subcommands. Each command is responsible for a certain parts of the omnifocus
    database, such as devices, tasks or the underlying storage. If you want more 
    information about those commands you can use `focus <command> --help` to display
    the help for that command.
""".trimIndent(),
    invokeWithoutSubcommand = true
) {
    init {
        subcommands(
            QueryCommand(),
            DeviceCommand(),
            CreateConfigCommand(),
            PerspectiveCommand(),
            StorageCommand(),
            TaskCommand(),
            VersionCommand(),
            LocateElementCommand()
        )
    }
}
