package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands

class DeviceCommand : NoOpCliktCommand(name = "devices", help = "Commands related to devices of the store", epilog = """
    The device command provides capabilities to manage devices registered to this
    omnifocus database. By default, focus does not create its own device in the
    database, however, for updating the database it is strongly suggested to do so.
""".trimIndent()) {
    init {
        subcommands(RegisterCommand(), DeviceListCommand(), UnregisterCommand(), RefreshDeviceCommand())
    }
}
