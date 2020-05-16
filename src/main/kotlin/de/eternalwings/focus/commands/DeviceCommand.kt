package de.eternalwings.focus.commands

import com.github.ajalt.clikt.core.NoOpCliktCommand
import com.github.ajalt.clikt.core.subcommands

class DeviceCommand : NoOpCliktCommand(name = "devices", help = "Commands related to devices of the store") {
    init {
        subcommands(RegisterCommand(), DeviceListCommand(), UnregisterCommand(), RefreshDeviceCommand())
    }
}
