package de.eternalwings.focus.commands

import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import de.eternalwings.focus.ErrorCodes
import de.eternalwings.focus.config.Configuration
import de.eternalwings.focus.failWith
import de.eternalwings.focus.storage.OmniDevice
import de.eternalwings.focus.storage.OmniStorage

class RefreshDeviceCommand : StorageBasedCommand(name = "refresh", help = "Updates a device to the newest changeset") {

    val device by argument("device", help = "The id of the device to update.").optional()

    override fun run() {
        val storage = loadStorage()
        val newestDevice = getDevice(storage)
        storage.updateDevice(newestDevice)
    }

    private fun getDevice(storage: OmniStorage): OmniDevice {
        val deviceId = device ?: Configuration.instance.device ?: failWith(
            "No device ID was specified.",
            ErrorCodes.GENERIC_ARGUMENT_ERROR
        )

        return storage.uniqueDevices.find { it.clientId == deviceId }
            ?: failWith("A device with the id $deviceId was not found.", ErrorCodes.GENERIC_ARGUMENT_ERROR)
    }

}
