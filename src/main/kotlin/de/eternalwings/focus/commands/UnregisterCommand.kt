package de.eternalwings.focus.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.config.config
import de.eternalwings.focus.config.save
import de.eternalwings.focus.failWith
import de.eternalwings.focus.prompt
import de.eternalwings.focus.storage.OmniStorage

class UnregisterCommand :
    StorageBasedCommand(name = "unregister", help = "Removes a client registration from the store") {

    val name by argument("name").optional()
    val confirm by option("--yes", "-y", help = "Confirm removal automatically").flag()

    override fun run() {
        val storage = loadStorage()
        val effectiveName = getDeviceName(storage)
        val device = storage.devices.find { it.name == effectiveName } ?: failWith(
            "No device with the given name found.",
            ErrorCodes.ELEMENT_NOT_FOUND
        )
        val input =
            confirm || prompt("Are you sure you want to delete '${effectiveName}' (Last sync: ${device.lastSync})? [y/n] ")
        if (input) {
            storage.removeDevice(device.clientId)
            if (config[Configuration.device] == device.clientId) {
                config[Configuration.device] = null
                config.save()
            }
        }
    }

    private fun getDeviceName(storage: OmniStorage): String {
        return name
            ?: config[Configuration.device]?.let { name -> storage.devices.firstOrNull { it.name == name }?.name }
            ?: failWith(
                "No device provided, have you registered one? Otherwise provide the id of the device.",
                ErrorCodes.GENERIC_ARGUMENT_ERROR
            )
    }
}
