package de.eternalwings.focus.commands.device

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import de.eternalwings.focus.commands.StorageBasedCommand
import de.eternalwings.focus.commands.WithDeviceNameCommand
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.prompt

class UnregisterCommand :
    StorageBasedCommand(name = "unregister", help = "Removes a client registration from the store"),
    WithDeviceNameCommand {

    override val deviceName by argument("name").optional()
    val confirm by option("--yes", "-y", help = "Confirm removal automatically").flag()

    override fun run() {
        val storage = loadStorage()
        val device = getDeviceByName(storage)
        val input =
            confirm || prompt("Are you sure you want to delete '${device.name}' (Last sync: ${device.lastSync})? [y/n] ")
        if (input) {
            storage.removeDevice(device.clientId)
            if (Configuration.instance.device == device.clientId) {
                Configuration.instance.device = null
                Configuration.save()
            }
        }
    }
}
