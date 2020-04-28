package de.eternalwings.focus.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.config.config
import de.eternalwings.focus.failWith

class UnregisterCommand :
    StorageBasedCommand(name = "unregister", help = "Removes a client registration from the store") {

    val name by argument("name").optional()

    override fun run() {
        val storage = loadStorage()
        val effectiveName = getDeviceName()
        val device = storage.devices.find { it.name == effectiveName } ?: failWith("No device with the given name found.", ErrorCodes.ELEMENT_NOT_FOUND)
        println("Are you sure you want to delete '${effectiveName}' (Last sync: ${device.lastSync})")
    }

    private fun getDeviceName(): String {
        return name ?: config[Configuration.device] ?: failWith(
            "No device provided, have you registered one? Otherwise provide the name of the device.",
            ErrorCodes.GENERIC_ARGUMENT_ERROR
        )
    }
}
